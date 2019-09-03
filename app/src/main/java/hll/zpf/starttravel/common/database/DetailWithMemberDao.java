package hll.zpf.starttravel.common.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import hll.zpf.starttravel.common.database.entity.DetailWithMember;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DETAIL_WITH_MEMBER".
*/
public class DetailWithMemberDao extends AbstractDao<DetailWithMember, Long> {

    public static final String TABLENAME = "DETAIL_WITH_MEMBER";

    /**
     * Properties of entity DetailWithMember.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MemberId = new Property(1, String.class, "memberId", false, "member_id");
        public final static Property DetailId = new Property(2, String.class, "detailId", false, "detail_id");
    }


    public DetailWithMemberDao(DaoConfig config) {
        super(config);
    }
    
    public DetailWithMemberDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DETAIL_WITH_MEMBER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"member_id\" TEXT," + // 1: memberId
                "\"detail_id\" TEXT);"); // 2: detailId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DETAIL_WITH_MEMBER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DetailWithMember entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String memberId = entity.getMemberId();
        if (memberId != null) {
            stmt.bindString(2, memberId);
        }
 
        String detailId = entity.getDetailId();
        if (detailId != null) {
            stmt.bindString(3, detailId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DetailWithMember entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String memberId = entity.getMemberId();
        if (memberId != null) {
            stmt.bindString(2, memberId);
        }
 
        String detailId = entity.getDetailId();
        if (detailId != null) {
            stmt.bindString(3, detailId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DetailWithMember readEntity(Cursor cursor, int offset) {
        DetailWithMember entity = new DetailWithMember( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // memberId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // detailId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DetailWithMember entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMemberId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDetailId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DetailWithMember entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DetailWithMember entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DetailWithMember entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}