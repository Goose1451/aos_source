package com.advantage.common.exceptions.token;

/**
 * Created by Evgeney Fiskin on 09-01-2016.
 */
public class WrongTokenTypeException extends GeneralTokenException {
    public WrongTokenTypeException() {
        super();
    }

    public WrongTokenTypeException(String s) {
        super(s);
    }
}
