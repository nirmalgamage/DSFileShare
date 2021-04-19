package com.semicolon.ds.handlers;

public interface CallBackWhenTimeout {
    void whenTimeout(String messageId);
    void onResponse(String messageId);
}
