package com.itartisan.api.beans.content.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public class StorageModel {

    @JsonIgnore
    private MultipartFile[] files;
    @JsonIgnore
    private MultipartFile file;
    private long fileSize;
    private String dataSetId;
    private String desc;
    private String mode;
    private boolean convert;           // 是否需要PDF转换
    private String uuid;               // UUID
    private String OriginalFilename;   // 原始文件名
    private String fileName;           // 上传原文件名
    private String fileExtension;      // 上传文件扩展名
    private String previewFileName;    // 预览文件名
    private String previewFilePath;    // 预览文路径
    private String dataSetPath;        // 数据集文路径
    private List<String> images;       // md文件名列表
    @JsonIgnore
    private File tempFile;             // 临时文件

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isConvert() {
        return convert;
    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOriginalFilename() {
        return OriginalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        OriginalFilename = originalFilename;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getPreviewFileName() {
        return previewFileName;
    }

    public void setPreviewFileName(String previewFileName) {
        this.previewFileName = previewFileName;
    }

    public String getPreviewFilePath() {
        return previewFilePath;
    }

    public void setPreviewFilePath(String previewFilePath) {
        this.previewFilePath = previewFilePath;
    }

    public String getDataSetPath() {
        return dataSetPath;
    }

    public void setDataSetPath(String dataSetPath) {
        this.dataSetPath = dataSetPath;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }
}
