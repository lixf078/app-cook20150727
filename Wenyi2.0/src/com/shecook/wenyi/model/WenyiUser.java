package com.shecook.wenyi.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class WenyiUser extends BaseModel implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7775924758641220984L;
	private String _ID; // 用户注册登录之后，后台返回唯一标识
	private String _mID; // mid 用户自动生成，用于标识唯一性
    private String _userguid;
    private String _email;
    private String _nickname;
    private String _password;
    private int _flag;
    private String _message;
    private String _uimage30;
    private String _uimage50;
    private String _uimage180;
    private String _score;
    private String _level;
    private String _msgcount;
    private String _level_core;
    private String _login_type;

    public WenyiUser() {
        super();
    }

    public WenyiUser(String _ID, String _mID, String token,String _userguid, String _email, String _nickname,
            String _password, int _flag, String _uimage30, String _uimage50,
            String _uimage180, String _score, String _level, String _msgcount) {
        super();
        this._ID = _ID;
        this._mID = _mID;
        this.token = token;
        this._userguid = _userguid;
        this._email = _email;
        this._nickname = _nickname;
        this._password = _password;
        this._flag = _flag;
        this._uimage30 = _uimage30;
        this._uimage50 = _uimage50;
        this._uimage180 = _uimage180;
        this._score = _score;
        this._level = _level;
        this._msgcount = _msgcount;
        
    }

    @Override
	protected void parseSelf(JSONObject jObject) throws JSONException {
		_ID = jObject.isNull("uid") ? "-1" : jObject.getInt("uid") + "";

		token = jObject.isNull("token") ? "" : jObject.getString("token");
	}

	public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String get_mID() {
		return _mID;
	}

	public void set_mID(String _mID) {
		this._mID = _mID;
	}

	public String get_userguid() {
        return _userguid;
    }

    public void set_userguid(String _userguid) {
        this._userguid = _userguid;
    }

    public String get_token() {
		return token;
	}

	public void set_token(String _token) {
		this.token = _token;
	}

	public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_nickname() {
        return _nickname;
    }

    public void set_nickname(String _nickname) {
        this._nickname = _nickname;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public int get_flag() {
        return _flag;
    }

    public void set_flag(int _flag) {
        this._flag = _flag;
    }

    public String get_uimage30() {
        return _uimage30;
    }

    public void set_uimage30(String _uimage30) {
        this._uimage30 = _uimage30;
    }

    public String get_uimage50() {
        return _uimage50;
    }

    public void set_uimage50(String _uimage50) {
        this._uimage50 = _uimage50;
    }

    public String get_uimage180() {
        return _uimage180;
    }

    public void set_uimage180(String _uimage180) {
        this._uimage180 = _uimage180;
    }

    public String get_score() {
        return _score;
    }

    public void set_score(String _score) {
        this._score = _score;
    }

    public String get_level() {
        return _level;
    }

    public void set_level(String _level) {
        this._level = _level;
    }

    public String get_msgcount() {
        return _msgcount;
    }

    public void set_msgcount(String _msgcount) {
        this._msgcount = _msgcount;
    }

	public String get_message() {
		return _message;
	}

	public void set_message(String _message) {
		this._message = _message;
	}

	public String get_level_core() {
		return _level_core;
	}

	public void set_level_core(String _level_core) {
		this._level_core = _level_core;
	}

	public String get_login_type() {
		return _login_type;
	}

	public void set_login_type(String _login_type) {
		this._login_type = _login_type;
	}

	@Override
	public String toString() {
		return "WenyiUser [_ID=" + _ID + ", _mID=" + _mID + ", _userguid="
				+ _userguid + ", _email=" + _email + ", _nickname=" + _nickname
				+ ", token=" + token + "]";
	}
	
}
