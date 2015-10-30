package com.intrafab.medicus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.intrafab.medicus.data.Account;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.loaders.MeLoader;
import com.intrafab.medicus.medJournal.fragments.PeriodCalendarEditFragment;
import com.intrafab.medicus.utils.FileUtils;
import com.intrafab.medicus.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class PaymentActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = PaymentActivity.class.getName();
    public static final String EXTRA_OPEN_PAYMENT = "openPayment";
    public final String userPhoto = "userPhoto";

    private static final int LOADER_ME_ID = 16;
    public static final int REQUEST_CODE_PICK_FILE = 500;
    public static final int REQUEST_CODE_PICK_IMAGE = 600;
    public static final int ABOUT_ME = 7;

    private TextView tvViewUserName;
    private TextView tvViewUserEmail;
    private TextView tvBirthday;
    private TextView tvAboutMe;
    private ImageView ivUserPhoto;
    private FloatingActionsMenu mActionsMenu;

    private android.app.LoaderManager.LoaderCallbacks<Account> mLoaderCallback = new android.app.LoaderManager.LoaderCallbacks<Account>() {
        @Override
        public android.content.Loader<Account> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ME_ID:
                    return createAccountLoader();
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<Account> loader, Account data) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    finishedAccountLoader(data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<Account> loader) {
            int id = loader.getId();
            switch (id) {
                case LOADER_ME_ID:
                    resetAccountLoader();
                    break;
                default:
                    break;
            }
        }
    };

    private android.content.Loader<Account> createAccountLoader() {
        Logger.d(TAG, "createAccountLoader");
        return new MeLoader(PaymentActivity.this);
    }

    private void finishedAccountLoader(Account data) {
        Logger.d(TAG, "finishedAccountLoader data: " + (data == null ? "NULL" : data.getName()));
        AppApplication.getApplication(this).setUserAccount(data);
        showUserName();
    }

    private void resetAccountLoader() {
        Logger.d(TAG, "resetAccountLoader");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setTitle(R.string.menu_account);
        showTransparentActionBar();
        setActionBarIcon(R.mipmap.ic_action_back);
        ViewCompat.setTransitionName(toolbar, EXTRA_OPEN_PAYMENT);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        tvViewUserName = (TextView) findViewById(R.id.tvUserName);
        tvViewUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        tvBirthday = (TextView) findViewById(R.id.tvBirthday);
        tvAboutMe = (TextView) findViewById(R.id.tvAboutMe);

        tvBirthday.setOnClickListener(this);
        tvAboutMe.setOnClickListener(this);

        ImageView ivBirthdatEdit = (ImageView) findViewById(R.id.ivEditBirthday);
        ivBirthdatEdit.setOnClickListener(this);

        ImageView ivEditAboutMe = (ImageView) findViewById(R.id.ivEditAboutMe);
        ivEditAboutMe.setOnClickListener(this);

        if (!showUserName())
            startLoadMe();

        setupUserInfo();

        mActionsMenu = (FloatingActionsMenu) this.findViewById(R.id.famEditPhoto);
        FloatingActionButton bGallery = (FloatingActionButton)findViewById(R.id.fabGallery);
        bGallery.setOnClickListener(this);

        FloatingActionButton bCamera = (FloatingActionButton)findViewById(R.id.fabCamera);
        bCamera.setOnClickListener(this);

        FloatingActionButton bDelete = (FloatingActionButton)findViewById(R.id.fabDelete);
        bDelete.setOnClickListener(this);

    }

    private boolean showUserName() {
        Account userAccount = AppApplication.getApplication(this).getUserAccount();
        if (userAccount != null) {
            String userName = userAccount.getName();

            if (!TextUtils.isEmpty(userName))
                tvViewUserName.setText(userName);
            else
                tvViewUserName.setText(getString(R.string.unknown_name));

            String userEmail = userAccount.getMail();
            if (!TextUtils.isEmpty(userEmail))
                tvViewUserEmail.setText(userEmail);
            else
                tvViewUserEmail.setText("");
            return true;
        } else {
            tvViewUserName.setText(getString(R.string.unknown_name));
            tvViewUserEmail.setText("");
            return false;
        }
    }

    private void setupUserInfo() {
        //setup photo
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        ivUserPhoto = (ImageView) findViewById(R.id.userPhoto);
        ivUserPhoto.setMaxHeight(height / 2);
        ivUserPhoto.setMinimumHeight(height / 2);
        Logger.d (TAG, "height:" + height);
        File file = new File(DBManager.getInstance().getDBPath(getApplicationContext()),userPhoto);
        if(file.exists()){
            Bitmap userPhotoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivUserPhoto.setImageBitmap(userPhotoBitmap);
        }
        Logger.d(TAG, "height image view:" + ivUserPhoto.getHeight());

        // setup birthday
        tvBirthday.setText(getBirthday());

        // setup aboutMe
        tvAboutMe.setText(getAboutMe());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_payment;
    }

    public static void launch(BaseActivity activity, View transitionView) {
        Intent intent = new Intent(activity, PaymentActivity.class);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        transitionView,
                        EXTRA_OPEN_PAYMENT
                );
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLoadMe() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().initLoader(LOADER_ME_ID, null, mLoaderCallback);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = getPickImageResultUri(data);

                String mLastFilePath = FileUtils.getPath(this, imageUri);
                if (TextUtils.isEmpty(mLastFilePath)) {
                    Logger.e(TAG, getResources().getString(R.string.error_file_not_selected));
                } else if (!checkFileFormat(mLastFilePath)) {
                    Logger.e(TAG, "REQUEST_CODE_PICK_IMAGE Selected file: " + mLastFilePath);
                } else
                    //ivUserPhoto.setImageURI(imageUri);
                    ivUserPhoto.setImageBitmap(chopAndResizeBitmap(imageUri));

            }
        } else if (requestCode == REQUEST_CODE_PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String mLastFilePath = FileUtils.getPath(this, uri);
                    if (TextUtils.isEmpty(mLastFilePath)) {
                        Logger.e(TAG, getResources().getString(R.string.error_file_not_selected));
                    } else if (!checkFileFormat(mLastFilePath)) {
                        Logger.e(TAG, "REQUEST_CODE_PICK_FILE Selected file: " + mLastFilePath);
                    } else
                        ivUserPhoto.setImageBitmap(chopAndResizeBitmap(uri));
                }
            }
        }
    }

   private void deletePhoto(){

       //setup photo
       Display display = getWindowManager().getDefaultDisplay();
       Point size = new Point();
       display.getSize(size);
       int height = size.y;
       ivUserPhoto.setMaxHeight(height / 2);
       ivUserPhoto.setMinimumHeight(height / 2);
       ivUserPhoto.setImageResource(R.mipmap.nophoto);

       File file = new File(DBManager.getInstance().getDBPath(getApplicationContext()),userPhoto);
       if (file.exists())
           file.delete();
   }

    public static Bitmap decodeSampledBitmapFromFile(String imageFilePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageFilePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

   private Bitmap chopAndResizeBitmap (Uri uri){
       try {

           Display display = getWindowManager().getDefaultDisplay();
           Point size = new Point();
           display.getSize(size);
           int screenWidth = size.x;
           int screenHeight = size.y;

           String path = FileUtils.getPath(getApplicationContext(), uri);

           Bitmap bitmap = decodeSampledBitmapFromFile(path, screenWidth, screenHeight / 2);

           // rotate image if it is necessary
           ExifInterface exif = new ExifInterface(path);
           int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
           Logger.d (TAG, "orientation: " + orientation);
           Matrix matrix = new Matrix();
           if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            matrix.postRotate(90);
           else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
               matrix.postRotate(180);
           else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
               matrix.postRotate(270);
           bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

           int imageHeight = bitmap.getHeight();
           int imageWidth = bitmap.getWidth();

           float scaleW = ((float)screenWidth)/imageWidth;
           float scaleH = ((float)screenHeight / 2)/imageHeight;
           if (imageHeight * scaleW < screenHeight / 2) {
               Logger.d(TAG, "picture has too big width");
               int newWidth = (int) (imageWidth * (scaleW / scaleH));
               bitmap = Bitmap.createBitmap(bitmap, (imageWidth - newWidth) / 2, 0, newWidth, imageHeight);
           }
           if (imageWidth * scaleH < screenWidth){
               Logger.d(TAG, "picture has too big height");
               int newHeight = (int) (imageHeight * (scaleH / scaleW));
               bitmap = Bitmap.createBitmap(bitmap, 0,(imageHeight - newHeight) / 2,  imageWidth, newHeight);
           }
           bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight / 2, true);
           saveImage(bitmap);
           return bitmap;

       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return null;
   }

    private boolean checkFileFormat(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return false;

        String extension = null;
        int index = filePath.lastIndexOf(".");
        if (index > 0 && index + 1 < filePath.length()) {
            extension = filePath.substring(index + 1);
            Logger.e(TAG, "checkFileFormat extension: " + extension);
        }

//        String mimeType = null;
//
//        if (!TextUtils.isEmpty(extension) && MimeTypeMap.getSingleton().hasExtension(extension)) {
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//            Logger.e(TAG, "checkFileFormat mimeType: " + (TextUtils.isEmpty(mimeType) ? "NULL" : mimeType));
//        }

        if (TextUtils.isEmpty(extension))
            return false;

        return true;

//        if (extension.equalsIgnoreCase("JPG")
//                || extension.equalsIgnoreCase("JPEG")
//                || extension.equalsIgnoreCase("PDF")
//                || extension.equalsIgnoreCase("DOC")
//                || extension.equalsIgnoreCase("DOCX")
//                || extension.equalsIgnoreCase("DCM")
//                || extension.equalsIgnoreCase("DICOM")
//                || extension.equalsIgnoreCase("ZIP")) {
//            return true;
//        }
//
//        return false;
    }

    private void saveImage(Bitmap image) {
        try {
            File file = new File(DBManager.getInstance().getDBPath(getApplicationContext()),userPhoto);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Logger.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        mActionsMenu.collapse();
        switch (v.getId()) {
            case R.id.fabGallery:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.fabCamera:
                startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.fabDelete:
                deletePhoto();
                break;
            case R.id.ivEditBirthday:
            case R.id.tvBirthday:
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, myCallBack, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.ivEditAboutMe:
            case R.id.tvAboutMe:
                PeriodCalendarEditFragment fragment = new PeriodCalendarEditFragment();
                fragment.setType(PaymentActivity.ABOUT_ME);
                fragment.setData(getAboutMe());
                fragment.show(getFragmentManager(), "about_me_dialog");
        }
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setBithday(dayOfMonth + "." + (monthOfYear+1) + "." + year);
        }
    };

    private Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // Create a chooser
        Intent chooserIntent = Intent.createChooser(allIntents.get(0), getResources().getString(R.string.chooser_header));
        allIntents.remove(0);
        // Add all other intents
        if (!allIntents.isEmpty())
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Logger.e(TAG, "getCaptureImageOutputUri start");
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();

        if (getImage != null) {
            if (!getImage.exists()) {
                getImage.mkdirs();
            }
            File imageFile = new File(getImage.getPath(), "pickImageResult.jpg");
            if (!imageFile.exists()) {
                try {
                    imageFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Logger.e(TAG, "getCaptureImageOutputUri imageFile: " + imageFile.getPath());
            outputFileUri = Uri.fromFile(imageFile);
            Logger.e(TAG, "getCaptureImageOutputUri outputFileUri: " + outputFileUri.getPath());
        }
        return outputFileUri;
    }

    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private String getBirthday (){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        return prefs.getString("birthday", "");
    }

    private void setBithday (String bithday){
        tvBirthday.setText(bithday);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("birthday", bithday);
        edit.commit();
    }

    private String getAboutMe (){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        return prefs.getString("aboutMe", "");
    }

    public void setAboutMe(String aboutMe){
        tvAboutMe.setText(aboutMe);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MEDICUS_APP", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("aboutMe", aboutMe);
        edit.commit();
    }

}