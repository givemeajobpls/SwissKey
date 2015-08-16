package maxirozay.com.swisskey;

/**
 * Created by Leto on 15/08/2015.
 */
public class SMSObject {
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_msg() {
        return _msg;
    }

    public void set_msg(String _msg) {
        this._msg = _msg;
    }

    public String get_readState() {
        return _readState;
    }

    public void set_readState(String _readState) {
        this._readState = _readState;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String get_folderName() {
        return _folderName;
    }

    public void set_folderName(String _folderName) {
        this._folderName = _folderName;
    }

    private String _id;
    private String _address;
    private String _msg;
    private String _readState; // "0" for have not read sms and "1" for have
    // read sms
    private String _time;
    private String _folderName;

    //+ getter and setter methods and

    @Override
    public String toString() {
        return "SMSObject [_id=" + _id + ", _address=" + _address + ", _msg="
                + _msg + ", _readState=" + _readState + ", _time=" + _time
                + ", _folderName=" + _folderName + "]";
    }
}