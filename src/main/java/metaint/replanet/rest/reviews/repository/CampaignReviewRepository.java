package metaint.replanet.rest.reviews.repository;

import metaint.replanet.rest.reviews.dto.CombineReviewDTO;
import metaint.replanet.rest.reviews.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CampaignReviewRepository extends JpaRepository<Campaign, Long> {

    @Query("SELECT c FROM reviewPkg_entityCampaign c ORDER BY c.campaignCode DESC")
    List<Campaign> findAllOrderedByCampaignCodeDesc();

    @Query(value = "SELECT c.* " +
            "FROM tbl_campaign_description c " +
            "WHERE c.campaign_code NOT IN ( " +
            "    SELECT r.campaign_code " +
            "    FROM tbl_review r " +
            "    WHERE r.campaign_code IS NOT NULL " +
            ") " +
            "AND c.end_date < CURRENT_TIMESTAMP", nativeQuery = true)
    List<Campaign> findUnassociatedCampaigns();
}
