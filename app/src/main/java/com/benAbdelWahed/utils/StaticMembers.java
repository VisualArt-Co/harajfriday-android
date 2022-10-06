package com.benAbdelWahed.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;

import com.benAbdelWahed.BuildConfig;
import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.LoginActivity;
import com.benAbdelWahed.responses.ErrorResponse;
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class StaticMembers {

    public static final String DATA = "data";
    public static final String ADDRESS = "address";
    public static final String COUNT = "count";
    public static final String ORDER = "order";
    public static final String WALLET_BALANCE = "balance";
    public static final String TYPE = "type";
    public static final int WALLET_CODE = 923;

    public static String toFloatFormat(float fl) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#.##", symbols).format(fl);
    }

    public enum paymentEnum {
        pay_by_wallet,
        MASTERCARD,
        VISA,
        MADA
    }

    public enum orderStatus {
        pending, in_progress, completed, cancelled
    }

    public static String getBaseURL() {
        return BuildConfig.BASE_URL;
    }

    public static String getBaseAPIURL() {
        return BuildConfig.BASE_URL + "api/customer/";
    }

    public static final String FIREBASE_BASE_URL = "https://fcm.googleapis.com/fcm/";

    public static final String USERS = "users";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_BACKEND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAYS = "D أيام و HH:mm:ss";
    public static final String DATE_FORMAT_TIME_ONLY = "HH:mm:ss";
    public static final String DATE_ONLY_FORMAT_VIEW = "dd/M/yyyy";
    public static final String DATE_AND_TIME_FORMAT_VIEW = "hh:mm a  dd MMM yyyy";
    public static final String DATE_FORMAT_BID = "dd MMM yy";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_ID = "product_id";
    public static final String IMAGES_PATH = "/BenAbdelWahed/images";
    public static final String PREMIUM = "premuim";
    public static final String INACTIVE = "inActive";
    public static final String COMMENT_ID = "comment_id";
    public static final String STATUS = "status";
    public static final String FROM_REGISTRATION = "from_registration";
    public static final String HARAJ = "haraj";
    public static final String CATEGORIES = "categories";
    public static final String COUNTRIES = "countries";
    public static final String LANGUAGE = "language";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String PAGE = "page";
    public static final String register = "signup";
    public static final String login = "login";
    public static final String forgetPassword = "foregetPassword";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final int PIX_CODE = 119;
    public static final int EDIT_CODE = 1003;
    public static final String ADD_Haraj = "addProduct";
    public static final String ALL_MAZAD = "allMazads";
    public static final String PRODUCTS = "products";
    public static final String SEARCH_PRODUCTS = "searchProducts";
    public static final String SEARCH_PRODUCTS_PAGINATION = "searchProductsPagination";
    public static final String IS_PREMIUM = "is_premium";
    public static final String PUBLIC = "public";
    public static final String ADD_INCREASE = "addIncrease";
    public static final String ADD_FAVORITE = "addFavorite";
    public static final String ADD_FOLLOW = "followProduct";
    public static final String REMOVE_FAVORITE = "removeFavorite";
    public static final String REMOVE_FOLLOW = "unFollowProduct";
    public static final String SUBSCRIBE = "requestToSubscribeInMazad";
    public static final String CONDITIONS = "conditions";
    public static final String SOCIAL_LINKS = "social_links";
    public static final String SUBSCRIBER_MAZAD = "subscribersInMazad";
    public static final String HARAJ_RESPONSE = "haraj_response";
    public static final String ADD_COMMENT = "addComment";
    public static final String ADD_RATE = "addRate";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String COMMENT = "comment";
    public static final String PRODUCT_DETAILS = "productDetails";
    public static final String RATE = "rate";
    public static final String PROFILE = "profile";
    public static final String BANNERS = "banners";
    public static final String CUSTOMER_BANNERS = "customerBanners";
    public static final String FAV_HARAJ = "favoirtes";
    public static final String MAZAD_DETAILS = "mazadDetails";
    public static final String UPDATE_PREMIUM = "upgradToPremuim";
    public static final String REPORT_COMMENT = "reportComment";
    public static final String PROBLEM = "problem";
    public static final String REPORT_PRODUCT = "reportProduct";
    public static final String LOG_OUT = "logout";
    public static final String CITIES = "cities";
    public static final String states = "states";
    public static final String SEND_CODE = "sendVerificationCode";
    public static final String SEND_CODE_FORGET = "sendVerificationCodeForegetPassword";
    public static final String CHECK_PHONE_CODE = "verifyCheck";
    public static final String CHECK_PHONE_CODE_FORGET = "verifyCodeForegetPassword";
    public static final String ACTION = "action";
    public static final String PHONE = "phone";
    public static final String CODE = "code";
    public static final String[] mazadTypes = {"running", "coming", "ended"};
    public static final String MAZAD = "mazad";
    public static final String POSITION = "position";
    @NotNull
    public static final String CITY = "city_id";
    @NotNull
    public static final String STATE = "state_id";
    @NotNull
    public static final String SUBCATEGORY_ID = "subcategory_id";
    public static final String SUBSUBCATEGORY_ID = "subsub_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String PER_PAGE = "per_page";
    public static final String CUSTOMER = "customer";
    @NotNull
    public static final String MESSAGES = "messages";
    @NotNull
    public static final String INFO = "info";
    @NotNull
    public static final String MAZAD_ID = "mazad_id";
    public static final String CONVERSATIONS = "conversations";
    public static final String OTHER_ID = "other id";
    @NotNull
    public static final String LAST_MESSAGE = "lastMessage";
    public static final String FIREBASE_TOKENS = "firebase_tokens";
    @Nullable
    public static final String BANNER = "banner";
    @NotNull
    public static final String MAZADS = "mazads";
    public static final String HARAJ_ID = "haraj_id";
    public static final String NOTIFICATION_LIST = "notificationList";
    public static final String DELETE_NOTI = "deleteAllNotification";
    public static final String EDIT_PROFILE = "editProfile";
    public static final String SETTINGS = "settings";
    public static final String CONTACT_US = "contactUs";
    public static final String FOLLOWED_PRODUCTS = "listFollowProduct";
    public static final String COUNT_UNREAD_NOTIFY = "countUnReadNotifications";
    public static final String RESET_COUNT_UNREAD_NOTIFICATIONS = "resetCountUnReadNotifications";
    public static final String REMOVE_COMMENT = "removeComment";
    public static final String REMOVE_PRODUCT = "removeProduct";
    @NotNull
    public static final String UNREAD_MESSAGES_COUNT = "unread_messages_count";
    public static final String DETAILS_BY_ID = "allDetailsById";
    public static final String TEXT = "text";
    public static final String MODEL = "model";
    public static final String DYNAMIC_PAGES = "dynamicPages";
    public static final String BANK_ACCOUNTS = "bankAccounts";
    public static final String PAYMENT = "payment";
    @Nullable
    public static final String STATE_NAME = "state name";
    public static final String EDIT_PRODUCT = "editProduct";
    public static final int BACKEND_DELAY_VAL = 0;
    public static final int EDIT_PROFILE_CODE = 364;
    public static final String PENDDING = "pendding";
    public static final String ACTIVITY = "activity";
    public static final String IS_ACTIVE = "is_active";
    public static final String LAST_ACTIVE_TIME = "last_active_time";
    public static final String CURRENT_DEALS = "current_deals";
    public static final String TOTAL_PRICE = "total_price";

    ////Get prof
    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static String getJson(Object o) {
        return new Gson().toJson(o);
    }

    public static Object getObjectFromJson(String json, Class<?> c) {
        try {
            if (json.isEmpty())
                return null;
            return new Gson().fromJson(json, c);
        } catch (Exception e) {
            return null;
        }
    }


    /////////////////Dates converter/////////////////////
    public static String changeDateFromIsoToView(String dateFrom) {
        SimpleDateFormat sdf = new SimpleDateFormat(StaticMembers.ISO_DATE_FORMAT, Locale.US);
        SimpleDateFormat sdfTo = new SimpleDateFormat(StaticMembers.DATE_FORMAT_BACKEND, Locale.getDefault());
        try {
            return sdfTo.format(sdf.parse(dateFrom));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFrom;
    }

    public static <T extends Activity> void startActivityOverAll(T activity, Class<?> destinationActivity) {
        Intent intent = new Intent(activity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public static <T extends Context> void startActivityOverAll(T activity, Class<?> destinationActivity) {
        Intent intent = new Intent(activity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static <T extends Activity> void startActivityOverAll(Intent intent, T activity) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public static <T extends View> void hideKeyboard(T view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static <A extends Activity> void hideKeyboard(A activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //////////////////////Password moveToAddHaraj/////////////////////
    public static InputFilter[] getInputTitleFilter(int max) {
        return new InputFilter[]{new EmojiExcludeFilter(max)};
    }

    private static class EmojiExcludeFilter implements InputFilter {
        int mMax;

        public EmojiExcludeFilter(int max) {
            mMax = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            int keep = mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                for (int i = start; i < end; i++) {
                    int type = Character.getType(source.charAt(i));
                    if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                        return "";
                    }
                }
                return null;
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        }

        public int getMax() {
            return mMax;
        }
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z-])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean checkValidationPassword(TextInputEditText editText, final TextInputLayout textInputLayout, final String errorMessage, final String errorMessage2) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    textInputLayout.setError(errorMessage);
                    textInputLayout.setErrorEnabled(true);
                } else {
                    if (!isValidPassword(s.toString())) {

                        textInputLayout.setError(errorMessage2);
                        textInputLayout.setErrorEnabled(true);
                    } else {
                        textInputLayout.setErrorEnabled(false);
                    }
                }

            }
        });
        if (TextUtils.isEmpty(editText.getText())) {
            textInputLayout.setError(errorMessage);
            textInputLayout.setErrorEnabled(true);
            return false;
        } else {
            if (!isValidPassword(editText.getText().toString())) {
                textInputLayout.setError(errorMessage2);
                textInputLayout.setErrorEnabled(true);
                return false;
            } else {
                textInputLayout.setErrorEnabled(false);
                return true;
            }
        }
    } //////////////////////Password moveToAddHaraj/////////////////////

    public static boolean isValidEmail(final String email) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[-A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkValidationEmail(TextInputEditText editText, final TextInputLayout textInputLayout, final String errorMessage, final String errorMessage2) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    textInputLayout.setError(errorMessage);
                    textInputLayout.setErrorEnabled(true);
                } else {
                    if (!isValidPassword(s.toString())) {

                        textInputLayout.setError(errorMessage2);
                        textInputLayout.setErrorEnabled(true);
                    } else {
                        textInputLayout.setErrorEnabled(false);
                    }
                }

            }
        });
        if (TextUtils.isEmpty(editText.getText())) {
            textInputLayout.setError(errorMessage);
            textInputLayout.setErrorEnabled(true);
            return false;
        } else {
            if (!isValidEmail(editText.getText().toString())) {
                textInputLayout.setError(errorMessage2);
                textInputLayout.setErrorEnabled(true);
                return false;
            } else {
                textInputLayout.setErrorEnabled(false);
                return true;
            }
        }
    }

    //////////////////////List From JSON/////////////////////

    static <T> ArrayList<T> getListFromJSON(String s, Class<T> c) {
        try {
            if (s.isEmpty())
                return new ArrayList<>();
            return new Gson().fromJson(s, new ListOfJson<T>(c));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static class ListOfJson<T> implements ParameterizedType {
        private Class<?> wrapped;

        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    //////////////////////Visiblity with Animation/////////////////////

    public static <V extends View> void makeVisible(V layout) {
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0.0f);
        layout.animate()
                .translationY(0)
                .alpha(1.0f)
                .setListener(null);
    }

    public static <V extends View> void makeGone(V layout) {
        layout.setVisibility(View.GONE);
    }
    //////////////////////Toasts/////////////////////

    private static Toast toast;

    public static void toastMessageShort(Context context, String messaage) {
        if (context != null) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void toastMessageShort(Context context, int messaage) {
        if (context != null) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void toastMessageShort(Context context, CharSequence messaage) {
        if (context != null) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, messaage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void toastMessageLong(Context context, int messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void toastMessageLong(Context context, String messaage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, messaage, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean checkTextInputEditText(TextInputEditText editText, final TextInputLayout textInputLayout, final String errorMessage) {
        return checkTextInputEditText(editText, textInputLayout, 0, errorMessage, errorMessage);
    }

    public static boolean checkTextInputEditText
            (TextInputEditText editText, final TextInputLayout textInputLayout, int min, final String errorMessage, final String errorMessageMin) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    textInputLayout.setError(errorMessage);
                    textInputLayout.setErrorEnabled(true);
                } else if (s.length() < min) {
                    textInputLayout.setError(errorMessageMin);
                    textInputLayout.setErrorEnabled(true);
                }
                {
                    textInputLayout.setErrorEnabled(false);
                }
            }
        });
        if (TextUtils.isEmpty(editText.getText())) {
            textInputLayout.setError(errorMessage);
            textInputLayout.setErrorEnabled(true);
            editText.requestFocus();
            return false;
        } else if (Objects.requireNonNull(editText.getText()).length() < min) {
            textInputLayout.setError(errorMessageMin);
            textInputLayout.setErrorEnabled(true);
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public static String getLanguage(Context context) {
        return "ar";
    }

    public static void setLanguage(Context context, String langStr) {
        PrefManager.getInstance(context).setStringData(LANGUAGE, langStr);
    }

    public static void changeLocale(Context context, String langStr) {
        setLanguage(context, langStr);
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(langStr.toLowerCase())); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
        Locale locale = context.getResources().getConfiguration().locale;
        Locale.setDefault(locale);
    }


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    public static String getTimeAgo(Date date) {
        long time = date.getTime();

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return "اللآن";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "الآن";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "منذ دقيقة";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return "منذ " + diff / MINUTE_MILLIS + " دقيقة";
        } else if (diff < 12 * HOUR_MILLIS) {
            return "منذ " + (diff / HOUR_MILLIS) + " ساعة";
        } else {
            /*if (diff < 24 * HOUR_MILLIS)
                return getDate(date, FROMAT_TIME_ONLY);
            else*/
            return getDate(date, DATE_AND_TIME_FORMAT_VIEW);
        }
    }


    public static String getTimeForCounter(long diff) {
        long dayConst = 3600000 * 24;
        StringBuilder stringBuilder = new StringBuilder();

        long days = diff / dayConst;
        long remainingMillis = diff - days * dayConst;
        if (remainingMillis > 0) {
            long hours = remainingMillis / (3600000);
            long minutes = (remainingMillis - hours * (3600000)) / 60000;
            long seconds =
                    (remainingMillis - hours * (3600000) - minutes * (60000)) / 1000;
            String s = String.format(Locale.US, "%02d:%02d:%02d:%02d ", days, hours, minutes, seconds);
            stringBuilder.append(s);
            if (days != 0)
                stringBuilder.append("يوم");
            else if (hours != 0)
                stringBuilder.delete(0, 3).append("ساعة");
            else if (minutes != 0)
                stringBuilder.delete(0, 6).append("دقيقة");
            else
                stringBuilder.delete(0, 9).append("ثانية");
        }
        return stringBuilder.toString();
    }

    public static String getTimeForCounterWithLine(long diff) {
        long dayConst = 3600000 * 24;
        StringBuilder stringBuilder = new StringBuilder();

        long days = diff / dayConst;
        long remainingMillis = diff - days * dayConst;
        if (remainingMillis > 0) {
            long hours = remainingMillis / (3600000);
            long minutes = (remainingMillis - hours * (3600000)) / 60000;
            long seconds =
                    (remainingMillis - hours * (3600000) - minutes * (60000)) / 1000;
            String s = String.format(Locale.US, "%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
            stringBuilder.append(s);
            if (days == 0)
                if (hours != 0)
                    stringBuilder.delete(0, 3);
                else if (minutes != 0)
                    stringBuilder.delete(0, 6);
                else
                    stringBuilder.delete(0, 9);
        }
        return stringBuilder.toString();
    }

    public static String getDaysLine(long diff) {
        long dayConst = 3600000 * 24;
        StringBuilder stringBuilder = new StringBuilder();

        long days = diff / dayConst;
        long remainingMillis = diff - days * dayConst;
        if (remainingMillis > 0) {
            long hours = remainingMillis / (3600000);
            long minutes = (remainingMillis - hours * (3600000)) / 60000;

            stringBuilder.append("ثانية\t\t\t");
            if (minutes != 0)
                stringBuilder.append("دقيقة\t\t\t");
            if (hours != 0)
                stringBuilder.append("ساعة\t\t\t");
            if (days != 0)
                stringBuilder.append("يوم\t\t\t");
        }
        return stringBuilder.toString();
    }

    public static String getTimeInText(Long diff) {

        long minsConst = 60000;
        long hoursConst = 3600000;
        long dayConst = hoursConst * 24;
        long weekConst = dayConst * 7;
        long monthConst = dayConst * 30;
        long yearsConst = monthConst * 12;

        long years = diff / (yearsConst);
        StringBuilder stringBuilder = new StringBuilder();

        if (years > 0)
            stringBuilder.append(String.format(getEnglishLocale(), "%d سنة", years));

        long months = (diff - years * yearsConst) / monthConst;
        if (months > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(" و ");
            stringBuilder.append(String.format(getEnglishLocale(), "%d شهر", months));
            if (years > 0)
                return stringBuilder.toString();
        }

        long weeks = (diff - years * yearsConst - months * monthConst) / weekConst;
        if (weeks > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(" و ");
            stringBuilder.append(String.format(getEnglishLocale(), "%d أسبوع", weeks));
            if (months > 0)
                return stringBuilder.toString();
        }

        long days = (diff - years * yearsConst - months * monthConst - weeks * weekConst) / dayConst;
        if (days > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(" و ");
            stringBuilder.append(String.format(getEnglishLocale(), "%d يوم", days));
            if (weeks > 0 || months > 0)
                return stringBuilder.toString();
        }

        long hours = (diff - years * yearsConst - months * monthConst - weeks * weekConst - days * dayConst) / hoursConst;
        if (hours > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(" و ");
            stringBuilder.append(String.format(getEnglishLocale(), "%d ساعة", hours));
            if (days > 0 || weeks > 0)
                return stringBuilder.toString();
        }

        long mins = (diff - years * yearsConst - months * monthConst - weeks * weekConst - days * dayConst - hours * hoursConst) / minsConst;
        if (mins > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(" و ");
            stringBuilder.append(String.format(getEnglishLocale(), "%d دقيقة", mins));

        }
        if (stringBuilder.length() == 0)
            stringBuilder.append("الآن");
        return stringBuilder.toString();
    }

    public static String getTimeForHaraj(Date date) {
        long time = date.getTime();
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return "الآن";
        }
        final long diff = now - time;
        return getTimeInText(diff);
    }

    public static String getDayAgo(Date date, Context context) {
        long time = date.getTime();
        long nowTime = Calendar.getInstance().getTimeInMillis();
        Calendar now = Calendar.getInstance();
        Calendar other = Calendar.getInstance();
        other.setTime(date);
        if (now.get(Calendar.DATE) == other.get(Calendar.DATE)) {
            return "اليوم";
        } else if (now.get(Calendar.DATE) - other.get(Calendar.DATE) == 1) {
            return "البارحة";
        } else {
            return getDate(date, DATE_ONLY_FORMAT_VIEW);
        }
    }

    public static String getDate() {
        return getDate(false);
    }

    public static String getDate(boolean dateOnly) {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat(dateOnly ? DATE_ONLY_FORMAT_VIEW : DATE_FORMAT_BACKEND, Locale.getDefault()).format(calendar.getTime());

    }

    public static String getDateFromBackend(String dateString) {
        try {
            assert dateString != null;
            return getTimeAgo(Objects.requireNonNull(new SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(Objects.requireNonNull(dateString))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateFromBackendForHaraj(String dateString) {
        try {
            assert dateString != null;
            return getTimeForHaraj(Objects.requireNonNull(new SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(Objects.requireNonNull(dateString))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateWithoutTimeFromBackend(@NotNull String dateString) {
        try {
            return new SimpleDateFormat(DATE_ONLY_FORMAT_VIEW, Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat(ISO_DATE_FORMAT, Locale.US).parse(dateString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.getDefault()).parse(date);
        } catch (ParseException ex) {
            return new Date();
        }
    }

    public static String getDate(@NotNull Calendar calendar) {
        return new SimpleDateFormat(DATE_ONLY_FORMAT_VIEW, Locale.getDefault()).format(calendar.getTime());
    }

    public static String getDate(@NotNull Date date) {
        return getDate(date, false);
    }

    public static String getDate(@NotNull Date date, boolean hasMonths) {
        return getDate(date, hasMonths ? DATE_FORMAT_BID : DATE_ONLY_FORMAT_VIEW);
    }

    public static String getDate(@NotNull Date date, String format) {
        return new SimpleDateFormat(format, getArabicLocale()).format(date);
    }

    public static Locale getArabicLocale() {
        return new Locale("ar");
    }

    public static Locale getEnglishLocale() {
        return Locale.US;
    }

    public static ArrayAdapter<String> getSpinnerAdapter(Context context, List<String> list) {
        return getSpinnerAdapter(context, list, true);
    }

    public static ArrayAdapter<String> getSpinnerAdapter(Context context, List<String> list, boolean hasHint) {
        return new ArrayAdapter<String>
                (context, R.layout.item_list_spinner, android.R.id.text1, list) {

            @NonNull
            @Override
            public View getView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                final View view;
                final TextView text;

                if (convertView == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_spinner, parent, false);
                } else {
                    view = convertView;
                }
                text = view.findViewById(android.R.id.text1);
                final String item = getItem(position);
                text.setText(getTextHTML(item));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                if (hasHint) {
                    if (position == 0) {
                        tv.setHint(getTextHTML(list.get(0)));
                        tv.setText("");
                    }
                }
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                return !hasHint || position != 0;
            }
        };
    }

    public static <T> boolean checkNotNullList(NestedScrollView scrollView, Context context, List<T> list, int selected, String s) {
        if (selected > -1 && list != null && !list.isEmpty()) {
            return true;
        } else {
            toastMessageShort(context, s);
            scrollView.scrollTo(0, scrollView.getBottom());
            return false;
        }
    }

    public static <T> boolean checkNotNullListSpinner
            (NestedScrollView scrollView, Spinner spinner, List<T> list, int selected, String s) {
        if (spinner != null && spinner.getSelectedView() != null) {
            TextView textView = spinner.getSelectedView().findViewById(android.R.id.text1);
            if (selected > -1 && list != null && !list.isEmpty()) {
                return true;
            } else {

                textView.setText(getTextHTML(s));
                scrollView.post(() -> scrollView.scrollTo(0, spinner.getBottom()));
                return false;
            }
        }
        return false;
    }

    public static CharSequence getTextHTML(String s) {
        return Html.fromHtml(s);
    }

    public static void showLoginDialog(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.need_login);
        alertDialog.setMessage(R.string.do_you_need_login);
        alertDialog.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> StaticMembers.startActivityOverAll(context, LoginActivity.class));
        alertDialog.setNegativeButton(context.getString(R.string.cancel), null);
        alertDialog.show();
    }

    public static void checkError(FragmentActivity activity, ResponseBody responseBody) {

        try {
            ErrorLoginResponse errorResponse = new GsonBuilder().create().fromJson(responseBody.string(), ErrorLoginResponse.class);
            if (errorResponse != null) {
                if (errorResponse.getError() != null && errorResponse.getError().toLowerCase().contains("unauthenticated"))
                    showLoginDialog(activity);
                else {
                    if (errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                        StaticMembers.toastMessageShort(activity, errorResponse.getMessage());
                    }
                    if (errorResponse.getData() != null && errorResponse.getData().getPrice() != null && !errorResponse.getData().getPrice().isEmpty())
                        toastMessageShort(activity, errorResponse.getData().getPrice().get(0));
                    if (errorResponse.getData() != null && errorResponse.getData().getPassword() != null && !errorResponse.getData().getPassword().isEmpty())
                        toastMessageShort(activity, errorResponse.getData().getPassword().get(0));
                    if (errorResponse.getData() != null && errorResponse.getData().getMazad_id() != null && !errorResponse.getData().getMazad_id().isEmpty())
                        toastMessageShort(activity, errorResponse.getData().getMazad_id().get(0));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            StaticMembers.toastMessageShort(activity, R.string.connection_error);
        }
    }
    public static void checkError(FragmentActivity activity, ResponseBody responseBody,boolean dataIsString) {
        try {
            if (dataIsString) {
                ErrorResponse errorResponse = new GsonBuilder().create().fromJson(responseBody.string(), ErrorResponse.class);
                if (errorResponse != null) {

                        if (errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                            StaticMembers.toastMessageShort(activity, errorResponse.getMessage());
                        }
                    if (errorResponse.getData() != null && errorResponse.getData().toLowerCase().contains("unauthenticated"))
                        showLoginDialog(activity);
                    else  toastMessageShort(activity, errorResponse.getData());
                }
            }
            else {
                ErrorLoginResponse errorResponse = new GsonBuilder().create().fromJson(responseBody.string(), ErrorLoginResponse.class);
                if (errorResponse != null) {
                    if (errorResponse.getError() != null && errorResponse.getError().toLowerCase().contains("unauthenticated"))
                        showLoginDialog(activity);
                    else {
                        if (errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                            StaticMembers.toastMessageShort(activity, errorResponse.getMessage());
                        }
                        if (errorResponse.getData() != null && errorResponse.getData().getPrice() != null && !errorResponse.getData().getPrice().isEmpty())
                            toastMessageShort(activity, errorResponse.getData().getPrice().get(0));
                        if (errorResponse.getData() != null && errorResponse.getData().getMazad_id() != null && !errorResponse.getData().getMazad_id().isEmpty())
                            toastMessageShort(activity, errorResponse.getData().getMazad_id().get(0));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            StaticMembers.toastMessageShort(activity, R.string.connection_error);
        }
    }

    public static ErrorLoginResponse getError(String error){
        try {
            return new GsonBuilder().create().fromJson(error, ErrorLoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void checkError(Context context, ErrorLoginResponse errorResponse) {

        try {
            if (errorResponse != null) {
                if (errorResponse.getError() != null && errorResponse.getError().toLowerCase().contains("unauthenticated"))
                    showLoginDialog(context);
                else {
                    if (errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                        StaticMembers.toastMessageShort(context, errorResponse.getMessage());
                    }
                    if (errorResponse.getData() != null && errorResponse.getData().getPrice() != null && !errorResponse.getData().getPrice().isEmpty())
                        toastMessageShort(context, errorResponse.getData().getPrice().get(0));
                    if (errorResponse.getData() != null && errorResponse.getData().getMazad_id() != null && !errorResponse.getData().getMazad_id().isEmpty())
                        toastMessageShort(context, errorResponse.getData().getMazad_id().get(0));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            StaticMembers.toastMessageShort(context, R.string.connection_error);
        }
    }
}
