package com.intrafab.medicus.db;

import android.content.Context;
import android.content.Loader;
import android.os.Environment;
import android.text.TextUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.intrafab.medicus.utils.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class DBManager {
    public static final String TAG = DBManager.class.getName();

    protected DB mSnappyDB;
    protected volatile List<Loader> mLoaders;

    private static class DBManagerHolder {
        private final static DBManager instance = new DBManager();
    }

    public static DBManager getInstance() {
        return DBManagerHolder.instance;
    }

    private String getDBPath(Context context) {
        String result = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String packageName = context.getPackageName();
            File dbPath = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + packageName);
            if (!dbPath.exists())
                dbPath.mkdirs();
            result = dbPath.getAbsolutePath();
        } else {
            File dbPath = Environment.getDataDirectory();
            if (!dbPath.exists())
                dbPath.mkdirs();
            result = dbPath.getAbsolutePath();
        }

        return result;
    }

    public <T> void registerObserver(Context context, Loader loader, Class<T> clazz) {
        if (loader == null) {
            new IllegalArgumentException("Registering loader is null");
        }

        if (mLoaders == null) {
            init(context);
        }

        Logger.e(TAG, "registerObserver loader: " + loader.getClass().getName());
        synchronized (mLoaders) {
            if (!mLoaders.contains(loader)) {
                Logger.e(TAG, "registerObserver loader added: " + loader.getClass().getName());
                mLoaders.add(loader);
            }
            Logger.e(TAG, "registerObserver loaders size: " + mLoaders.size());
        }
    }

    public <T> void unregisterObserver(Loader loader, Class<T> clazz) {
        if (loader == null) {
            new IllegalArgumentException("Unregistering loader is null");
        }

        if (mLoaders == null)
            return;

        Logger.e(TAG, "unregisterObserver loader: " + loader.getClass().getName());
        synchronized (mLoaders) {
            if (mLoaders.contains(loader)) {
                Logger.e(TAG, "unregisterObserver loader remove: " + loader.getClass().getName());
                mLoaders.remove(loader);
            }
            Logger.e(TAG, "registerObserver loaders size: " + mLoaders.size());
        }

        if (mLoaders != null && mLoaders.size() == 0) {
            close();
        }
    }

    public synchronized boolean isOpen() {
        try {
            return mSnappyDB != null && mSnappyDB.isOpen();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        return false;
    }

    public synchronized void init(Context context) {
        try {
            if (mLoaders == null)
                mLoaders = Collections.synchronizedList(new ArrayList<Loader>());
            if (mSnappyDB == null)
                mSnappyDB = new SnappyDB.Builder(context)
                        .directory(getDBPath(context))
                        .name(TAG)
                        .build();
        } catch (SnappydbException e) {
            e.printStackTrace();
            Logger.e(TAG, "Can't create database");
        }
    }

    public synchronized void close() {
        if (mLoaders == null
                || (mLoaders != null && mLoaders.size() <= 0)) {
            if (mSnappyDB != null) {
                try {
                    mSnappyDB.close();
                    mSnappyDB = null;
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }

            if (mLoaders != null) {
                mLoaders.clear();
                mLoaders = null;
            }
        }
    }

    public synchronized <T> void insertObject(Context context, String key, Object object, Class<T> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                if (isExistsKey(key))
                    deleteObject(key);

                JsonElement element = new Gson().toJsonTree(object, clazz);
                String json = new Gson().toJson(element);
                Logger.e(TAG, "Insert into database key: " + key + "; object: " + json);
                mSnappyDB.put(key, json);
                if (mLoaders != null) {
                    synchronized (mLoaders) {
                        Iterator i = mLoaders.iterator();
                        while (i.hasNext())
                            ((Loader) i.next()).onContentChanged();
                    }
                }

            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't insert into database key: " + key + "; object: " + object);
            }
        }
    }

    public synchronized <T, L> void insertArrayObject(Context context, Class<L> clazzLoader,  String key, List<T> listObjects, Class<T> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                String json = new Gson().toJson(listObjects, new TypeToken<List<T>>() {
                }.getType());
                mSnappyDB.put(key, json);
                Logger.d(TAG, "Insert data to database key: " + key);

                onContentChanged(clazzLoader);
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't insert array into database key: " + key + "; object: " + listObjects);
            }
        }
    }

    private <L> void onContentChanged(Class<L> clazzLoader) {
        if (mLoaders != null) {
            synchronized (mLoaders) {
                Logger.e(TAG, "onContentChanged class: " + clazzLoader.getName());
                Iterator i = mLoaders.iterator();
                while (i.hasNext()) {
                    Loader loader =  (Loader) i.next();
                    Logger.e(TAG, "onContentChanged found: " + loader.getClass().getName());
                    if (loader.getClass().isAssignableFrom(clazzLoader)) {
                        Logger.e(TAG, "onContentChanged sync: " + clazzLoader.getName());
                        loader.onContentChanged();
                        break;
                    }
                }
            }
        }
    }

    public synchronized <T> void insertArrayObject(Context context, String key, List<T> listObjects, Class<T> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                String json = new Gson().toJson(listObjects, new TypeToken<List<T>>() {
                }.getType());
                mSnappyDB.put(key, json);
                Logger.d(TAG, "Insert data to database key: " + key);

                if (mLoaders != null) {
                    synchronized (mLoaders) {
                        Iterator i = mLoaders.iterator();
                        while (i.hasNext())
                            ((Loader) i.next()).onContentChanged();
                    }
                }
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't insert array into database key: " + key + "; object: " + listObjects);
            }
        }
    }

    public synchronized <T> void insertArrayObject(Context context, String key, T[] listObjects) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                List<T> list = new ArrayList<T>(Arrays.asList(listObjects));
                String json = new Gson().toJson(list, new TypeToken<List<T>>() {}.getType());
                mSnappyDB.put(key, json);

                if (mLoaders != null) {
                    synchronized (mLoaders) {
                        Iterator i = mLoaders.iterator();
                        while (i.hasNext())
                            ((Loader) i.next()).onContentChanged();
                    }
                }
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't insert array into database key: " + key + "; object: " + listObjects);
            }
        }
    }

    public synchronized <T> T readObject(Context context, String key, Class<T> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                String json = mSnappyDB.get(key);

                Logger.e(TAG, "Read from database key: " + key + "; object: " + json);

                JsonElement element = new Gson().fromJson(json, JsonElement.class);
                return new Gson().fromJson(element, clazz);

            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read from database key: " + key + "; clazz: " + clazz.getName());
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read json database key: " + key);
            }
        }

        return null;
    }

    public synchronized <T> T[] readArray(Context context, String key, Class<T[]> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                if (!isExistsKey(key))
                    return null;

                String json = mSnappyDB.get(key);
                return new Gson().fromJson(json, clazz);
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read from database key: " + key + "; clazz: " + clazz.getName());
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read json database key: " + key);
            }
        }

        return null;
    }

    public synchronized <T> List<T> readArrayToList(Context context, String key, Class<T[]> clazz) {
        if (!isOpen()) {
            init(context);
        }
        if (mSnappyDB != null) {
            try {
                if (!isExistsKey(key))
                    return null;

                Logger.d(TAG, "Read data from database key: " + key);
                String json = mSnappyDB.get(key);
                T[] arr = new Gson().fromJson(json, clazz);
                return Arrays.asList(arr);
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read from database key: " + key + "; clazz: " + clazz.getName());
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't read json database key: " + key);
            }
        }

        return null;
    }

    public synchronized boolean isExistsKey(String key) {
        if (mSnappyDB != null) {
            try {
                return mSnappyDB.exists(key);
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't find database key: " + key);
            }
        }

        return false;
    }

    public synchronized void deleteObject(String key) {
        if (mSnappyDB != null) {
            try {
                mSnappyDB.del(key);
                if (mLoaders != null) {
                    synchronized (mLoaders) {
                        Iterator i = mLoaders.iterator();
                        while (i.hasNext())
                            ((Loader) i.next()).onContentChanged();
                    }
                }
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't delete from database key: " + key);
            }
        }
    }

    public synchronized <L> void deleteObject(String key, Class<L> clazzLoader) {
        if (mSnappyDB != null) {
            try {
                mSnappyDB.del(key);
                Logger.e(TAG, "Delete from database key: " + key);
                onContentChanged(clazzLoader);
            } catch (SnappydbException e) {
                e.printStackTrace();
                Logger.e(TAG, "Can't delete from database key: " + key);
            }
        }
    }
}
