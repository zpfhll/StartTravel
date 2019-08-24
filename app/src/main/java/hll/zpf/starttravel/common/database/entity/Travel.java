package hll.zpf.starttravel.common.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import hll.zpf.starttravel.common.Utils;
import org.greenrobot.greendao.annotation.*;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import hll.zpf.starttravel.common.database.DaoSession;
import hll.zpf.starttravel.common.database.MemberDao;
import hll.zpf.starttravel.common.database.DetailDao;
import hll.zpf.starttravel.common.database.StepDao;
import hll.zpf.starttravel.common.database.TravelDao;

@Entity
public class Travel {
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
    @Property(nameInDb = "money")
    private float money;

    /**
     * 0:未启程 1：旅途中 2：结束未清算完成 3：结束并且清算完成
     */
    @Property(nameInDb = "state")
    private int state;
    @Property(nameInDb = "image")
    private byte[] image;
    @Property(nameInDb = "user_id")
    private String userId;

    @ToMany(joinProperties = {
            @JoinProperty(name = "id",referencedName = "stepTravelId")
    })
    private List<Step> stepList;

    @ToMany(joinProperties = {
            @JoinProperty(name = "id",referencedName = "detailTravelId")
    })
    private List<Detail> detailList;

    @ToMany(joinProperties = {
            @JoinProperty(name = "id",referencedName = "memberTravelId")
    })
    private List<Member> memberList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1728782803)
    private transient TravelDao myDao;


    public Travel(){
        Utils utils = new Utils();
        this.id = "T"+ utils.getDateStringByFormat("",null);
    }

    @Generated(hash = 1954945903)
    public Travel(String id, String name, String memo, String startDate,
            String endDate, float money, int state, byte[] image, String userId) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.money = money;
        this.state = state;
        this.image = image;
        this.userId = userId;
    }

    @Override
    public String toString() {
        String str = "id:" + id + " Name:" + name + " memo:" + memo
                + " startDate:" + startDate + " endDate:" + endDate
                + " money:" + money+ " state:" + state+ " image:" + image.length
                + " userId:" + userId;
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

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1335843578)
    public List<Step> getStepList() {
        if (stepList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StepDao targetDao = daoSession.getStepDao();
            List<Step> stepListNew = targetDao._queryTravel_StepList(id);
            synchronized (this) {
                if (stepList == null) {
                    stepList = stepListNew;
                }
            }
        }
        return stepList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2055851112)
    public synchronized void resetStepList() {
        stepList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1762531415)
    public List<Detail> getDetailList() {
        if (detailList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DetailDao targetDao = daoSession.getDetailDao();
            List<Detail> detailListNew = targetDao._queryTravel_DetailList(id);
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 855850224)
    public List<Member> getMemberList() {
        if (memberList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MemberDao targetDao = daoSession.getMemberDao();
            List<Member> memberListNew = targetDao._queryTravel_MemberList(id);
            synchronized (this) {
                if (memberList == null) {
                    memberList = memberListNew;
                }
            }
        }
        return memberList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1794216443)
    public synchronized void resetMemberList() {
        memberList = null;
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
    @Generated(hash = 1336027131)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTravelDao() : null;
    }

}
