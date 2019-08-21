package hll.zpf.starttravel.common.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import hll.zpf.starttravel.common.Utils;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Step {
    @Id
    @Property(nameInDb = "id")
    private String id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "memo")
    private String memo;
    @Property(nameInDb = "start_date")
    private String startDate;
    @Property(nameInDb = "end_date")
    private String endDate;
    @Property(nameInDb = "latitude")
    private float latitude;
    @Property(nameInDb = "longitude")
    private int longitude;
    @Property(nameInDb = "image")
    private byte[] image;
    @Property(nameInDb = "travel_id")
    private String stepTravelId;


    public Step(){

    }

    @Generated(hash = 57955863)
    public Step(String id, String name, String memo, String startDate, String endDate,
            float latitude, int longitude, byte[] image, String stepTravelId) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.stepTravelId = stepTravelId;
    }

    @Override
    public String toString() {
        String str = "id:" + id + " Name:" + name + " memo:" + memo
                + " startDate:" + startDate + " endDate:" + endDate
                + " latitude:" + latitude+ " longitude:" + longitude+ " image:" + image.length
                + " travelId:" + stepTravelId;
        return str;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Bitmap getImageBitma() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void setImageBitmap(Bitmap icon) {
        this.image =  new Utils().bitmapToBytes(icon);
    }

    public String getStepTravelId() {
        return stepTravelId;
    }

    public void setStepTravelId(String stepTravelId) {
        this.stepTravelId = stepTravelId;
    }
}
