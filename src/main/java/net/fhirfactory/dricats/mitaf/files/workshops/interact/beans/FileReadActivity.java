/*
 * Copyright (c) 2022 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.mitaf.files.workshops.interact.beans;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class FileReadActivity {
    private static final Logger LOG = LoggerFactory.getLogger(FileReadActivity.class);

    private DateTimeFormatter timeFormatter;

    private DataParcelTypeDescriptor contentDescriptor;
    private String fileSourceDirectory;
    private String fileNamePrefix;
    private String fileNameSuffix;
    private String fileActivitySuccessDirectory;
    private String fileActivityFailDirectory;
    private String fileActivityInProgressDirectory;
    private boolean deleteAfterActivitySuccess;

    //
    // Constructor(s)
    //

    public FileReadActivity(){
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS").withZone(ZoneId.of(PetasosPropertyConstants.DEFAULT_TIMEZONE));
        this.contentDescriptor = null;
        this.fileSourceDirectory = null;
        this.fileNamePrefix = null;
        this.fileNameSuffix = null;
        this.fileActivitySuccessDirectory = null;
        this.fileActivityFailDirectory = null;
        this.fileActivityInProgressDirectory = null;
        this.deleteAfterActivitySuccess = false;
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public DataParcelTypeDescriptor getContentDescriptor(){
        return(this.contentDescriptor);
    }

    public void setContentDescriptor(DataParcelTypeDescriptor contentDescriptor){
        this.contentDescriptor = contentDescriptor;
    }

    public boolean hasFileSourceDirectory(){
        boolean hasValue = this.fileSourceDirectory != null;
        return(hasValue);
    }

    public String getFileSourceDirectory() {
        return fileSourceDirectory;
    }

    public void setFileSourceDirectory(String fileSourceDirectory) {
        this.fileSourceDirectory = fileSourceDirectory;
    }

    public boolean hasFileNamePrefix(){
        boolean hasValue = this.fileNamePrefix != null;
        return(hasValue);
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public boolean hasFileNameSuffix(){
        boolean hasValue = this.fileNameSuffix != null;
        return(hasValue);
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

    public boolean hasFileActivitySuccessDirectory(){
        boolean hasValue = this.fileActivitySuccessDirectory != null;
        return(hasValue);
    }

    public String getFileActivitySuccessDirectory() {
        return fileActivitySuccessDirectory;
    }

    public void setFileActivitySuccessDirectory(String fileActivitySuccessDirectory) {
        this.fileActivitySuccessDirectory = fileActivitySuccessDirectory;
    }

    public boolean hasFileActivityFailDirectory(){
        boolean hasValue = this.fileActivityFailDirectory != null;
        return(hasValue);
    }

    public String getFileActivityFailDirectory() {
        return fileActivityFailDirectory;
    }

    public void setFileActivityFailDirectory(String fileActivityFailDirectory) {
        this.fileActivityFailDirectory = fileActivityFailDirectory;
    }

    public boolean hasFileActivityInProgressDirectory(){
        boolean hasValue = this.fileActivityInProgressDirectory != null;
        return(hasValue);
    }

    public String getFileActivityInProgressDirectory() {
        return fileActivityInProgressDirectory;
    }

    public void setFileActivityInProgressDirectory(String fileActivityInProgressDirectory) {
        this.fileActivityInProgressDirectory = fileActivityInProgressDirectory;
    }

    public boolean isDeleteAfterActivitySuccess() {
        return deleteAfterActivitySuccess;
    }

    public void setDeleteAfterActivitySuccess(boolean deleteAfterActivitySuccess) {
        this.deleteAfterActivitySuccess = deleteAfterActivitySuccess;
    }

    //
    // Business Logic
    //
}
