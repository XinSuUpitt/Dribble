package suxin.dribble.dribble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import suxin.dribble.model.Attachment;
import suxin.dribble.model.Bucket;
import suxin.dribble.model.Comment;
import suxin.dribble.model.Followee;
import suxin.dribble.model.Like;
import suxin.dribble.model.Shot;
import suxin.dribble.model.User;
import suxin.dribble.utils.ModelUtil;

/**
 * Created by suxin on 10/21/16.
 */

public class Dribbble {

    private static final String TAG = "Dribbble API";

    public static final int COUNT_PER_LOAD = 12;

    private static final String API_URL = "https://api.dribbble.com/v1/";

    private static final String USER_END_POINT = API_URL + "user";
    private static final String USERS_END_POINT = API_URL + "users";
    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";

    private static final String SP_AUTH = "auth";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final String KEY_SHOT_ID = "shot_id";
    private static final String KEY_USER = "user";

    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<Shot> SHOT_TYPE = new TypeToken<Shot>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};
    private static final TypeToken<List<Comment>> COMMENT_LIST_TYPE = new TypeToken<List<Comment>>(){};
    private static final TypeToken<List<Followee>> FOLLOWER_LIST_TYPE = new TypeToken<List<Followee>>(){};
    private static final TypeToken<List<Attachment>> ATTACHMENTS_LIST_TYPE = new TypeToken<List<Attachment>>(){};
    private static final TypeToken<Attachment> ATTACHMENT_TYPE_TOKEN = new TypeToken<Attachment>(){};
    private static final TypeToken<List<User>> USERS_LIST_TYPE = new TypeToken<List<User>>(){};

    private static OkHttpClient client = new OkHttpClient();

    private static String accessToken;
    private static User user;

    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeRequest(Request request) throws DribbbleException {
        try {
            Response response = client.newCall(request).execute();
            //Log.d(TAG, response.header("X-RateLimit-Remaining"));
            return response;
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }
    }


    private static Response makeGetRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url).build();
        return makeRequest(request);
    }

    private static Response makePostRequest(String url,
                                            RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url,
                                           RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .put(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete()
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url,
                                              RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
        return makeRequest(request);
    }

    private static <T> T parseResponse(Response response,
                                       TypeToken<T> typeToken) throws DribbbleException {
        String responseString;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }

        Log.d(TAG, responseString);

        try {
            return ModelUtil.toObject(responseString, typeToken);
        } catch (JsonSyntaxException e) {
            throw new DribbbleException(responseString);
        }
    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws DribbbleException {
        if (response.code() != statusCode) {
            throw new DribbbleException(response.message());
        }
    }

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static boolean isLoggedIn() {
        return accessToken != null;
    }

    public static void login(@NonNull Context context,
                             @NonNull String accessToken) throws DribbbleException {
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);

        Dribbble.user = getUser();
        storeUser(context, user);
    }

    public static void logout(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);

        accessToken = null;
        user = null;
    }

    public static User getCurrentUser() {
        return user;
    }


    public static User getUser() throws DribbbleException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    public static User getSingleUser(String userid) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userid;
        return parseResponse(makeGetRequest(url), USER_TYPE);
    }

    public static List<User> getUsers(int page, String userId) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userId + "/following?page=" + page;
        return parseResponse(makeGetRequest(url), USERS_LIST_TYPE);
    }

    public static List<Like> getLikes(int page) throws DribbbleException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }

    public static List<Comment> getComments(int page, String shotTitle) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotTitle + "/comments?page=" + page;
        return parseResponse(makeGetRequest(url), COMMENT_LIST_TYPE);
    }

    public static List<Attachment> getAttachments(int page, String shotTitle) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotTitle + "/attachments?page=" + page;
        return parseResponse(makeGetRequest(url), ATTACHMENTS_LIST_TYPE);
    }

    public static Attachment getAttachment(String shotid, String attachmentid) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotid + "/attachments/" + attachmentid;
        return parseResponse(makeGetRequest(url), ATTACHMENT_TYPE_TOKEN);
    }

    public static List<Followee> getFollowers(int page, String userid) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userid + "/followers?page=" + page;
        return parseResponse(makeGetRequest(url), FOLLOWER_LIST_TYPE);
    }

    public static List<Shot> getLikedShots(int page) throws DribbbleException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }

    public static List<Shot> getAnimatedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAttachmentsShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getDebutsShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getTeamShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getPlayoffsShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getReboundsShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getAnimatedGifsRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAnimatedGifsMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAnimatedGifsMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAttachmentsRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getDebutsRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getTeamRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getPlayoffsRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getReboundsRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }


    public static List<Shot> getShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getRecentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostCommentedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?sort=comments&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAlltimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewedAlltimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAnimatedGifsPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsAlltimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedGifsMostViewedAlltimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=animated&timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getAttachmentsPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=views&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=views&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=views&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAttachmentsMostViewedAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=attachments&sort=views&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }


    public static List<Shot> getDebutsPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getDebutsMostViewedAlltimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=debuts&timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getTeamPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamPastAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getTeamMostViewedAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=teams&timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }


    public static List<Shot> getPlayoffsPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getPlayoffsMostViewedAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=playoffs&timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }


    public static List<Shot> getReboundsPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=week&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=month&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=year&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=ever&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostViewedPastWeekShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=week&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostViewedPastMonthShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=month&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostViewedPastYearShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=year&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }
    public static List<Shot> getReboundsMostViewedAllTimeShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=rebounds&timeframe=ever&sort=views&page=" + page;
        return parseResponse(makeGetRequest(url),SHOT_LIST_TYPE);
    }

    public static List<Shot> getFollowringShots(int page) throws DribbbleException {
        String url = USER_END_POINT + "/following/shots?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Shot> getShotsOfUser(int page, String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id + "/shots?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }


    public static Shot getShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    public static Like likeShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makePostRequest(url, new FormBody.Builder().build());

        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);

        return parseResponse(response, LIKE_TYPE);
    }

    public static void unlikeShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void followUser(@NonNull String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id + "/follow";
        Response response = makePutRequest(url, new FormBody.Builder().build());

        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);

    }

    public static void unfollowUser(@NonNull String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id + "/follow";
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static boolean isLikingShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }

    public static boolean isFollowingUser(@NonNull String id) throws DribbbleException {
        String url = USER_END_POINT+ "/following/" + id;
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_NO_CONTENT:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }

    public static List<Shot> getTagsShot(String tag, int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "/tags/" + tag + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static User getUser(String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id;
        return parseResponse(makeGetRequest(url), USER_TYPE);
    }

    public static List<Bucket> getUserBuckets(int page) throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * Will return all the buckets for the logged in user
     * @return
     * @throws DribbbleException
     */
    public static List<Bucket> getUserBuckets() throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets(@NonNull String userId,
                                              int page) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getShotBuckets(@NonNull String shotId,
                                              int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * Will return all the buckets for a certain shot
     * @param shotId
     * @return
     * @throws DribbbleException
     */
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket newBucket(@NonNull String name,
                                   @NonNull String description) throws DribbbleException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
    }

    public static void deleteBucket(@NonNull String bucketid) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketid;
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static List<Shot> getBucketShots(@NonNull String bucketId,
                                            int page) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static void storeAccessToken(@NonNull Context context, @Nullable String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtil.save(context, KEY_USER, user);
    }

    public static User loadUser(@NonNull Context context) {
        return ModelUtil.read(context, KEY_USER, new TypeToken<User>(){});
    }
}
