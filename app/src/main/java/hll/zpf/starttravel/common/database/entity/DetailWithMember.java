package hll.zpf.starttravel.common.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DetailWithMember {
    @Id
    private Long id;
    @Property(nameInDb = "member_id")
    private String memberId;
    @Property(nameInDb = "detail_id")
    private String detailId;

    public DetailWithMember() {
    }

    @Generated(hash = 2049368530)
    public DetailWithMember(Long id, String memberId, String detailId) {
        this.id = id;
        this.memberId = memberId;
        this.detailId = detailId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }
}
