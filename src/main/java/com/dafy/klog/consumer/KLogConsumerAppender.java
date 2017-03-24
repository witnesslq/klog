package com.dafy.klog.consumer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.google.common.base.Strings;

import java.io.File;

/**
 * Created by Caedmon on 2016/4/18.
 */
public class KLogConsumerAppender extends RollingFileAppender {
    private String serviceName;
    private String address;
    private String fileNamePattern;
    private String logPattern;
    private String appenderName;
    private String logDir;
    private static final String FILE_SP=File.separator;
    public KLogConsumerAppender(LoggerContext context, String appenderName, String serviceName,
                                String address,String fileNamePattern, String logPattern,String logDir){
        this.appenderName=appenderName;
        this.serviceName=serviceName;
        this.address=address;
        this.fileNamePattern=fileNamePattern;
        this.logPattern=logPattern;
        this.context=context;
        this.logDir=logDir;
    }
    public void start(){
        if(isStarted()){
            return;
        }
        setName(appenderName);
        if(Strings.isNullOrEmpty(logDir)){
            logDir="logs";
        }
        setFile(logDir+ FILE_SP+serviceName+FILE_SP+serviceName+"-"+address+".log");
        TimeBasedRollingPolicy policy=new TimeBasedRollingPolicy<>();
        policy.setFileNamePattern(fileNamePattern);
        policy.setParent(this);
        policy.setContext(context);
        policy.start();
        this.setRollingPolicy(policy);
        PatternLayoutEncoder encoder=new PatternLayoutEncoder();
        encoder.setContext(context);
        setEncoder(encoder);
        encoder.setPattern(logPattern);
        encoder.start();
        super.start();
    }
}
