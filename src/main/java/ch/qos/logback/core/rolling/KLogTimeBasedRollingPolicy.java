package ch.qos.logback.core.rolling;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.RenameUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by Caedmon on 2017/3/30.
 */
public class KLogTimeBasedRollingPolicy<E>  extends TimeBasedRollingPolicy<E> {
    private String serviceName;
    private String addr;
    private RenameUtil klogRenameUtil=new RenameUtil();
    public KLogTimeBasedRollingPolicy(String serviceName,String addr){
        this.serviceName=serviceName;
        this.addr=addr;
        this.klogRenameUtil.setContext(this.getContext());
    }
    @Override
    public void rollover() throws RolloverFailure {
        String elapsedPeriodsFileName = getTimeBasedFileNamingAndTriggeringPolicy()
                .getElapsedPeriodsFileName();
        elapsedPeriodsFileName=elapsedPeriodsFileName
                .replaceAll("%PARSER_ERROR\\[sn\\]",this.serviceName)
                .replaceAll("%PARSER_ERROR\\[addr\\]",this.addr);

        String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
        if (compressionMode == CompressionMode.NONE) {
            if (getParentsRawFileProperty() != null) {
                klogRenameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
            } // else { nothing to do if CompressionMode == NONE and parentsRawFileProperty == null }
        } else {
            if (getParentsRawFileProperty() == null) {
                future = asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
            } else {
                future = renamedRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
            }
        }
        ArchiveRemover remover=timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
        if (remover != null) {
            remover.clean(new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
        }
    }
}
