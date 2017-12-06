package indi.gavin.orm;

import java.util.Arrays;
import java.util.List;

public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int status = BizStatus.S_ERROR_UNDEFINED;
    List<Object> args = null;

    private String exceptionMsg;

    public BizException(String msg) {
        super(msg);
        exceptionMsg = msg;
    }

    public BizException(int status) {
        super();
        this.status = status;
    }

    public BizException(int status, Object... args) {
        super();
        this.status = status;

        this.args = Arrays.asList(args);
    }

    public BizException(int status, Throwable causedBy) {
        super(causedBy);
        this.status = status;
    }

    public BizException(int status, Throwable causedBy, Object... args) {
        super(causedBy);
        this.status = status;

        this.args = Arrays.asList(args);
    }

    public int getStatus() {
        return status;
    }

    public List<Object> getArguments() {
        return this.args;
    }

    @Override
    public String getMessage() {
        if (exceptionMsg != null) {
            return exceptionMsg;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("{\"status\":").append(status);
        if (args != null && args.size() > 0) {
            sb.append(",args:[");
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('"').append(String.valueOf(args.get(i))).append('"');
            }
            sb.append(']');
        }
        sb.append('}');

        return sb.toString();
    }

}
