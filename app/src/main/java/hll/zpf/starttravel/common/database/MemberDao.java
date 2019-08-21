package hll.zpf.starttravel.common.database;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import hll.zpf.starttravel.common.database.entity.DetailWithMember;

import hll.zpf.starttravel.common.database.entity.Member;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEMBER".
*/
public class MemberDao extends AbstractDao<Member, String> {

    public static final String TABLENAME = "MEMBER";

    /**
     * Properties of entity Member.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "id");
        public final static Property Name = new Property(1, String.class, "name", false, "name");
        public final static Property Money = new Property(2, float.class, "money", false, "money");
        public final static Property Image = new Property(3, byte[].class, "image", false, "image");
        public final static Property MemberTravelId = new Property(4, String.class, "memberTravelId", false, "travel_id");
    }

    private DaoSession daoSession;

    private Query<Member> detail_MemberListQuery;
    private Query<Member> travel_MemberListQuery;

    public MemberDao(DaoConfig config) {
        super(config);
    }
    
    public MemberDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEMBER\" (" + //
                "\"id\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"name\" TEXT," + // 1: name
                "\"money\" REAL NOT NULL ," + // 2: money
                "\"image\" BLOB," + // 3: image
                "\"travel_id\" TEXT);"); // 4: memberTravelId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEMBER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Member entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindDouble(3, entity.getMoney());
 
        byte[] image = entity.getImage();
        if (image != null) {
            stmt.bindBlob(4, image);
        }
 
        String memberTravelId = entity.getMemberTravelId();
        if (memberTravelId != null) {
            stmt.bindString(5, memberTravelId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Member entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindDouble(3, entity.getMoney());
 
        byte[] image = entity.getImage();
        if (image != null) {
            stmt.bindBlob(4, image);
        }
 
        String memberTravelId = entity.getMemberTravelId();
        if (memberTravelId != null) {
            stmt.bindString(5, memberTravelId);
        }
    }

    @Override
    protected final void attachEntity(Member entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public Member readEntity(Cursor cursor, int offset) {
        Member entity = new Member( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.getFloat(offset + 2), // money
            cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3), // image
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // memberTravelId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Member entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMoney(cursor.getFloat(offset + 2));
        entity.setImage(cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3));
        entity.setMemberTravelId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Member entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(Member entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Member entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "memberList" to-many relationship of Detail. */
    public List<Member> _queryDetail_MemberList(String detailId) {
        synchronized (this) {
            if (detail_MemberListQuery == null) {
                QueryBuilder<Member> queryBuilder = queryBuilder();
                queryBuilder.join(DetailWithMember.class, DetailWithMemberDao.Properties.MemberId)
                    .where(DetailWithMemberDao.Properties.DetailId.eq(detailId));
                detail_MemberListQuery = queryBuilder.build();
            }
        }
        Query<Member> query = detail_MemberListQuery.forCurrentThread();
        query.setParameter(0, detailId);
        return query.list();
    }

    /** Internal query to resolve the "memberList" to-many relationship of Travel. */
    public List<Member> _queryTravel_MemberList(String memberTravelId) {
        synchronized (this) {
            if (travel_MemberListQuery == null) {
                QueryBuilder<Member> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.MemberTravelId.eq(null));
                travel_MemberListQuery = queryBuilder.build();
            }
        }
        Query<Member> query = travel_MemberListQuery.forCurrentThread();
        query.setParameter(0, memberTravelId);
        return query.list();
    }

}
