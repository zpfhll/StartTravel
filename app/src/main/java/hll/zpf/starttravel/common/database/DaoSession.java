package hll.zpf.starttravel.common.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import hll.zpf.starttravel.common.database.entity.User;
import hll.zpf.starttravel.common.database.entity.DetailWithMember;
import hll.zpf.starttravel.common.database.entity.Member;
import hll.zpf.starttravel.common.database.entity.Step;
import hll.zpf.starttravel.common.database.entity.Detail;
import hll.zpf.starttravel.common.database.entity.Travel;

import hll.zpf.starttravel.common.database.UserDao;
import hll.zpf.starttravel.common.database.DetailWithMemberDao;
import hll.zpf.starttravel.common.database.MemberDao;
import hll.zpf.starttravel.common.database.StepDao;
import hll.zpf.starttravel.common.database.DetailDao;
import hll.zpf.starttravel.common.database.TravelDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig detailWithMemberDaoConfig;
    private final DaoConfig memberDaoConfig;
    private final DaoConfig stepDaoConfig;
    private final DaoConfig detailDaoConfig;
    private final DaoConfig travelDaoConfig;

    private final UserDao userDao;
    private final DetailWithMemberDao detailWithMemberDao;
    private final MemberDao memberDao;
    private final StepDao stepDao;
    private final DetailDao detailDao;
    private final TravelDao travelDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        detailWithMemberDaoConfig = daoConfigMap.get(DetailWithMemberDao.class).clone();
        detailWithMemberDaoConfig.initIdentityScope(type);

        memberDaoConfig = daoConfigMap.get(MemberDao.class).clone();
        memberDaoConfig.initIdentityScope(type);

        stepDaoConfig = daoConfigMap.get(StepDao.class).clone();
        stepDaoConfig.initIdentityScope(type);

        detailDaoConfig = daoConfigMap.get(DetailDao.class).clone();
        detailDaoConfig.initIdentityScope(type);

        travelDaoConfig = daoConfigMap.get(TravelDao.class).clone();
        travelDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        detailWithMemberDao = new DetailWithMemberDao(detailWithMemberDaoConfig, this);
        memberDao = new MemberDao(memberDaoConfig, this);
        stepDao = new StepDao(stepDaoConfig, this);
        detailDao = new DetailDao(detailDaoConfig, this);
        travelDao = new TravelDao(travelDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(DetailWithMember.class, detailWithMemberDao);
        registerDao(Member.class, memberDao);
        registerDao(Step.class, stepDao);
        registerDao(Detail.class, detailDao);
        registerDao(Travel.class, travelDao);
    }
    
    public void clear() {
        userDaoConfig.clearIdentityScope();
        detailWithMemberDaoConfig.clearIdentityScope();
        memberDaoConfig.clearIdentityScope();
        stepDaoConfig.clearIdentityScope();
        detailDaoConfig.clearIdentityScope();
        travelDaoConfig.clearIdentityScope();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public DetailWithMemberDao getDetailWithMemberDao() {
        return detailWithMemberDao;
    }

    public MemberDao getMemberDao() {
        return memberDao;
    }

    public StepDao getStepDao() {
        return stepDao;
    }

    public DetailDao getDetailDao() {
        return detailDao;
    }

    public TravelDao getTravelDao() {
        return travelDao;
    }

}