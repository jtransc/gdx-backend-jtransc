package com.jtransc.media.limelibgdx.profiler;

import com.jtransc.JTranscSystem;

public class ProfileTime {

	private String name;
	private double timeStamp;
	private double sumTimes = 0;
	private double countTimes = 0;

	public ProfileTime(String name) {
		this.name = name;
		start();
	}

	public void start() {
		timeStamp = JTranscSystem.stamp();
	}

	public void stop() {
		double time = JTranscSystem.stamp() - timeStamp;
		sumTimes += time;
		countTimes++;
		System.out.println("Timer " + name + ": time=" + time + ";sum=" + sumTimes + "; average=" + (sumTimes / countTimes));
	}
}