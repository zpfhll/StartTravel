package hll.zpf.starttravel.common.database.entity;

import org.greenrobot.greendao.annotation.*;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import hll.zpf.starttravel.common.database.DaoSession;
import hll.zpf.starttravel.common.database.MemberDao;
import hll.zpf.starttravel.common.database.DetailDao;

@Entity
public class Detail {
    @Id
    @Property(nameInDb = "id")
    private String id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "memo")
    private String memo;
    @Property(nameInDb = "date")
    private String date;
    @Property(nameInDb = "money")
    private float money;
    @Property(nameInDb = "travel_id")
    private String detailTravelId;
    @ToMany
    @JoinEntity(
            entity = DetailWithMember.class,
            sourceProperty = "detailId",
            targetProperty = "memberId"
    )
    private List<Member> memberList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1914425134)
    private transient DetailDao myDao;


    public Detail() {
    }

    @Generated(hash = 253554145)
    public Detail(String id, String name, String memo, String date, float money,
            String detailTravelId) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.date = date;
        this.money = money;
        this.detailTravelId = detailTravelId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getDetailTravelId() {
        return detailTravelId;
    }

    public void setDetailTravelId(String detailTravelId) {
        this.detailTravelId = detailTravelId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1818307067)
    public List<Member> getMemberList() {
        if (memberList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MemberDao targetDao = daoSession.getMemberDao();
            List<Member> memberListNew = targetDao._queryDetail_MemberList(id);
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
    @Generated(hash = 454049436)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDetailDao() : null;
    }
}
