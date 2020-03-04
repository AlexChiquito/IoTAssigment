package com.aitia.demo.car_common.dto;

import java.io.Serializable;

public class sensorData implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int data;
	private String SensorType;
	private long timeStamp;

    public sensorData() {    }

    public sensorData(int data, String sensorType, long timeStamp) {
        this.data = data;
        SensorType = sensorType;
        this.timeStamp = timeStamp;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getSensorType() {
        return SensorType;
    }

    public void setSensorType(String sensorType) {
        SensorType = sensorType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

	
}
