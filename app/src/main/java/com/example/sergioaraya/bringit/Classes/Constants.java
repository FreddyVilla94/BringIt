package com.example.sergioaraya.bringit.Classes;

/**
 * Created by SergioAraya on 20/09/2017.
 */

public class Constants {

    private static Constants constants;

    private static final String TAG_IMG = "IMG";
    private static final String TAG_MA= "MainActivity";
    private static final String TAG_SLSA = "ShopListsActivity";

    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int RC_SIGN_IN = 9001;

    // REST Services.
    private static final String URL_GETUSERS = "https://listas-comprasb.herokuapp.com/getUsers";
    private static final String URL_GETSHOPLISTSUSER = "https://listas-comprasb.herokuapp.com/getShopListsUser/";
    private static final String URL_LOGINSOCIALNETWORK = "https://listas-comprasb.herokuapp.com/loginSocialNetwork";
    private static final String URL_SAVESHOPLIST = "https://listas-comprasb.herokuapp.com/saveShopList";
    private static final String URL_SAVEUSER = "https://listas-comprasb.herokuapp.com/saveUser";
    private static final String URL_AUTHENTICATE = "https://listas-comprasb.herokuapp.com/authenticate";
    private static final String URL_DELETESHOPLIST = "https://listas-comprasb.herokuapp.com/deleteShopList/";
    private static final String URL_UPDATESHOPLIST = "https://listas-comprasb.herokuapp.com/updateShopList/";
    private static final String URL_SAVEPRODUCT = "https://listas-comprasb.herokuapp.com/saveProduct";
    private static final String URL_DELETEPRODUCTSHOPLIST = "https://listas-comprasb.herokuapp.com/deleteProduct/";
    private static final String URL_UPDATEPRODUCTSHOPLIST = "https://listas-comprasb.herokuapp.com/updateProduct/";
    private static final String URL_UPDATESTATEPRODUCTSHOPLIST = "https://listas-comprasb.herokuapp.com/updateStateProduct/";
    private static final String URL_SHARESHOPLIST = "https://listas-comprasb.herokuapp.com/shareShopList/";

    // Regular expression to validate the user email input.
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*$";

    // Regular expression to validate the user password input.
    private static final String PATTERN_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)([A-Za-z\\d]){8,12}$";

    private static final String PATTERN_NUMBER = ".*\\d.*";

    private Constants() {}

    public static synchronized Constants getInstance() {

        if (constants == null){

            constants = new Constants();

        }

        return constants;
    }

    public static String getTagImg() {
        return TAG_IMG;
    }

    public static String getTagMA() {
        return TAG_MA;
    }

    public static String getUrlGetUsers() {
        return URL_GETUSERS;
    }

    public static String getUrlGetShopListsUser() {
        return URL_GETSHOPLISTSUSER;
    }

    public static String getUrlLoginSocialNetwork() { return URL_LOGINSOCIALNETWORK; }

    public static String getUrlSaveShopList() { return URL_SAVESHOPLIST; }

    public static String getUrlSaveUser() { return URL_SAVEUSER; }

    public static String getUrlAuthenticate() { return URL_AUTHENTICATE; }

    public static String getUrlDeleteShopList() { return URL_DELETESHOPLIST; }

    public static String getUrlUpdateShopList() { return URL_UPDATESHOPLIST; }

    public static String getUrlSaveProduct() { return URL_SAVEPRODUCT; }

    public static String getPatternEmail(){ return PATTERN_EMAIL; }

    public static String getPatternPassword(){ return PATTERN_PASSWORD; }

    public static String getTagSLsA() { return TAG_SLSA; }

    public static int getRcSignIn() { return RC_SIGN_IN; }

    public static String getUrlDeleteProductShopList() { return URL_DELETEPRODUCTSHOPLIST; }

    public static String getUrlUpdateProductShopList() { return URL_UPDATEPRODUCTSHOPLIST; }

    public static String getUrlUpdateStateProductShopList() { return URL_UPDATESTATEPRODUCTSHOPLIST; }

    public static String getUrlShareShopList() { return URL_SHARESHOPLIST; }

    public static String getPatternNumber() { return PATTERN_NUMBER; }
}
