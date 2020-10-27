package com.zwzch.fool.engine.router.value;

public class LobValue implements Comparable<String>{
	

	    private String str;
	    private String introducer;

	    public LobValue(String str, String introducer){
	        this.str = str;
	        this.introducer = introducer;
	    }

	    @Override
	    public int compareTo(String o) {
	        return this.str.compareTo(o);
	    }

	    public String getStr() {
	        return str;
	    }

	    public String getIntroducer() {
	        return introducer;
	    }
}
