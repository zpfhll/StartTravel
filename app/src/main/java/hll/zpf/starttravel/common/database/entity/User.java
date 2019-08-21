package hll.zpf.starttravel.common.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import hll.zpf.starttravel.common.Utils;
import org.greenrobot.greendao.annotation.*;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import hll.zpf.starttravel.common.database.DaoSession;
import hll.zpf.starttravel.common.database.TravelDao;
import hll.zpf.starttravel.common.database.UserDao;

@Entity
public class User {
    @Id
    @Property(nameInDb = "id")
    private String id;
    @Property(nameInDb = "mail_address")
    private String mailAddress;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "introduce")
    private String introduce;
    @Property(nameInDb = "image")
    private byte[] image;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id",referencedName = "userId")
    })
    List<Travel> travelList;
    @Property(nameInDb = "visitor")
    private Boolean isVisitor;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    public User(){
        Utils utils = new Utils();
        this.id = "U"+ utils.getDateStringByFormat("",null);
    }

    @Generated(hash = 1010879522)
    public User(String id, String mailAddress, String name, String introduce, byte[] image,
            Boolean isVisitor) {
        this.id = id;
        this.mailAddress = mailAddress;
        this.name = name;
        this.introduce = introduce;
        this.image = image;
        this.isVisitor = isVisitor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Bitmap getImageBitma() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void setImageBitmap(Bitmap icon) {
        this.image =  new Utils().bitmapToBytes(icon);
    }

    @Override
    public String toString() {
        String str = "mail:" + mailAddress + " Name:" + name + " introduce:" + introduce + " id:" + id;
        return str;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1253686112)
    public List<Travel> getTravelList() {
        if (travelList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TravelDao targetDao = daoSession.getTravelDao();
            List<Travel> travelListNew = targetDao._queryUser_TravelList(id);
            synchronized (this) {
                if (travelList == null) {
                    travelList = travelListNew;
                }
            }
        }
        return travelList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 309704425)
    public synchronized void resetTravelList() {
        travelList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

    public Boolean getIsVisitor() {
        return this.isVisitor;
    }

    public void setIsVisitor(Boolean isVisitor) {
        this.isVisitor = isVisitor;
    }
}
