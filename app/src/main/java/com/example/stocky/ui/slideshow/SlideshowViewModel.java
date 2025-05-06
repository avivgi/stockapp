package com.example.stocky.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.stocky.stockValue.StockValueAPI;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Loading...");

        StockValueAPI.getStockValueFromAPI("AAPL", new StockValueAPI.StockValueCallback() {
            @Override
            public void onSuccess(long price) {
                // Update LiveData using postValue
                mText.postValue(String.valueOf(price));
            }

            @Override
            public void onError(Exception e) {
                mText.postValue("Error fetching data");
                e.printStackTrace();
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }
}
