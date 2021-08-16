package java_spring_boot.hw7.repositories;

import java_spring_boot.hw7.entities.Brands;
import java_spring_boot.hw7.entities.Items;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ItemsRepository extends JpaRepository<Items, Long>{

    List<Items> findByPriceGreaterThan(double price);
    List<Items> findAll(Sort sort);
    List<Items> findAllByNameContainingOrderByPriceAsc(String name);
    List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceDesc(double price1, double price2, String name);
    List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceAsc(double price1, double price2, String name);

    List<Items> findAllByPriceBetweenAndNameContainingAndBrandOrderByPriceAsc(double price1, double price2, String name,Brands brands_id);
    List<Items> findAllByPriceBetweenAndNameContainingAndBrandOrderByPriceDesc(double price1, double price2, String name,Brands brands_id);

    List<Items> findAllByBrandOrderByPriceAsc(Brands brands_id);
    List<Items> findAllByBrandOrderByPriceDesc(Brands brands_id);



}
