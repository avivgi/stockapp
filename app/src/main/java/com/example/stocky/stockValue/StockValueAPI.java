package com.example.stocky.stockValue;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class StockValueAPI {

    private static final String TAG = "StockValueAPI";
    private static final OkHttpClient client = new OkHttpClient();

    public interface StockValueCallback {
        void onSuccess(long price);
        void onError(Exception e);
    }

    /**
     * Asynchronously fetches the stock value from the Alpha Vantage API.
     *
     * @param stockName the stock symbol.
     * @param callback  a callback to receive the result.
     */
    public static void getStockValueFromAPI(String stockName, final StockValueCallback callback) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                + stockName + "&apikey=G8UTAZBDJL6V03UV";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        String errorMsg = "Unexpected response: " + response;
                        Log.e(TAG, errorMsg);
                        if (callback != null) {
                            callback.onError(new IOException(errorMsg));
                        }
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.has("Global Quote")) {
                            JSONObject globalQuote = jsonResponse.getJSONObject("Global Quote");
                            if (globalQuote.has("05. price")) {
                                double price = globalQuote.getDouble("05. price");
                                if (callback != null) {
                                    callback.onSuccess((long) price);
                                }
                                return;
                            } else {
                                String msg = "Key '05. price' not found in JSON response.";
                                Log.e(TAG, msg);
                                if (callback != null) {
                                    callback.onError(new Exception(msg));
                                }
                            }
                        } else {
                            String msg = "Key 'Global Quote' not found in JSON response.";
                            Log.e(TAG, msg);
                            if (callback != null) {
                                callback.onError(new Exception(msg));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error", e);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in onResponse", e);
                    if (callback != null) {
                        callback.onError(e);
                    }
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network call failed", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
}