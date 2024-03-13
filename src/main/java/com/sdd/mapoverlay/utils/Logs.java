package com.sdd.mapoverlay.utils;

public class Logs{
	public String type = "DEL";
	public Segment segment;
	
	public Logs(Segment segment, String type){
		this.segment = segment;
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public Segment getSegment(){
		return this.segment;
	}
	
	@Override
	public String toString(){
		String desc = "DELETED: ";
		if (this.type=="ADD")
			desc = "ADDED: ";
		return desc + this.segment.toString();
	}
	
}
