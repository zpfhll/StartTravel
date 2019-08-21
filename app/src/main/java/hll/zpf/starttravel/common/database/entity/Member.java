package hll.zpf.starttravel.common.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import hll.zpf.starttravel.common.Utils;
import org.greenrobot.greendao.annotation.*;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import hll.zpf.starttravel.common.database.DaoSession;
import hll.zpf.starttravel.common.database.DetailDao;
import hll.zpf.starttravel.common.database.MemberDao;

@Entity
public class Member {
    @Id
    @Property(nameInDb = "id")
    private String id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "money")
    private float money;
    @Property(nameInDb = "image")
    private byte[] image;
    @Property(nameInDb = "travel_id")
    private String memberTravelId;
    @ToMany
    @JoinEntity(
            entity = DetailWithMember.class,
            sourceProperty = "memberId",
            targetProperty = "detailId"
    )
    private List<Detail> detailList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1200613910)
    private transient MemberDao myDao;


    public Member() {
        Utils utils = new Utils();
        this.id = "M"+ utils.getDateStringByFormat("",null);
    }

    @Generated(hash = 852275197)
    public Member(String id, String name, float money, byte[] image,
            String memberTravelId) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.image = image;
        this.memberTravelId = memberTravelId;
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

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Bitmap getImageBitmap() {
        if(image == null || image.length < 1){
            return null;
        }
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void setImageBitmap(Bitmap icon) {
        this.image =  new Utils().bitmapToBytes(icon);
    }

    public String getMemberTravelId() {
        return memberTravelId;
    }

    public void setMemberTravelId(String memberTravelId) {
        this.memberTravelId = memberTravelId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 517536673)
    public List<Detail> getDetailList() {
        if (detailList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DetailDao targetDao = daoSession.getDetailDao();
            List<Detail> detailListNew = targetDao._queryMember_DetailList(id);
            synchronized (this) {
                if (detailList == null) {
                    detailList = detailListNew;
                }
            }
        }
        return detailList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1287854973)
    public synchronized void resetDetailList() {
        detailList = null;
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
    @Generated(hash = 1742104579)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMemberDao() : null;
    }
}
