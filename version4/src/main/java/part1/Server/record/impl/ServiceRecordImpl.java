package part1.Server.record.impl;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import part1.common.pojo.RecordInfo;
import part1.Server.record.ServiceRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class ServiceRecordImpl implements ServiceRecord {

    List<RecordInfo> recordInfo;

    public ServiceRecordImpl() {
        this.recordInfo = new ArrayList<>();
    }

    @Override
    public Integer getTimes(String serviceName) {
        return recordInfo.size();
    }

    @Override
    public List<RecordInfo>  getRecord() {
        return recordInfo;
    }

    public void addRecord(RecordInfo recordInfo){
        this.recordInfo.add(recordInfo);
    }
}
