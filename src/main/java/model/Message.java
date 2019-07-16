package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

/**
 * Created by pupi on 16/07/19.
 */
public class Message {
    private String content;
    private Date createdAt;
    private Long crc32 = 0L;

    public Message(String content) {
        this.content = content;
        this.createdAt = Calendar.getInstance().getTime();
        this.crc32 = calculateCRC32();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.crc32 = calculateCRC32();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Long getCRC32() {
        return crc32;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long calculateCRC32() {
        CRC32 crc = new CRC32();
        crc.update(this.toString().getBytes());
        return crc.getValue();
    }
}