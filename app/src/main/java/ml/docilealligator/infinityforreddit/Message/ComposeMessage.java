package ml.docilealligator.infinityforreddit.Message;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ml.docilealligator.infinityforreddit.API.RedditAPI;
import ml.docilealligator.infinityforreddit.Utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ComposeMessage {
    public static void composeMessage(Retrofit oauthRetrofit, String accessToken, Locale locale, String username,
                                      String subject, String message, ComposeMessageListener composeMessageListener) {
        Map<String, String> headers = APIUtils.getOAuthHeader(accessToken);
        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.API_TYPE_KEY, "json");
        params.put(APIUtils.RETURN_RTJSON_KEY, "true");
        params.put(APIUtils.SUBJECT_KEY, subject);
        params.put(APIUtils.TEXT_KEY, message);
        params.put(APIUtils.TO_KEY, username);

        oauthRetrofit.create(RedditAPI.class).composePrivateMessage(headers, params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseMessage.parseComposedMessageError(response.body(), new ParseMessage.ParseComposedMessageErrorListener() {
                        @Override
                        public void noError() {
                            composeMessageListener.composeMessageSuccess();
                        }

                        @Override
                        public void error(String errorMessage) {
                            composeMessageListener.composeMessageFailed(errorMessage);
                        }
                    });
                    Log.i("asfsdaf", "s " + response.body());
                } else {
                    composeMessageListener.composeMessageFailed(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                composeMessageListener.composeMessageFailed(t.getMessage());
            }
        });
    }

    public interface ComposeMessageListener {
        void composeMessageSuccess();
        void composeMessageFailed(String errorMessage);
    }
}