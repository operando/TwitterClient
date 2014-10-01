package com.android.twitter.models;


import com.android.twitter.TwitterParameter;

import lombok.Data;

@Data
public class AsyncResult<D> {
    private Exception exception;
    private D data;
    private TwitterParameter.ERROR error;
}
