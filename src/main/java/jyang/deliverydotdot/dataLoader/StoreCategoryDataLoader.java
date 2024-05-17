package jyang.deliverydotdot.dataLoader;

import java.util.List;
import jyang.deliverydotdot.domain.StoreCategory;
import jyang.deliverydotdot.repository.StoreCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreCategoryDataLoader implements CommandLineRunner {

  private final StoreCategoryRepository storeCategoryRepository;

  @Override
  public void run(String... args) {
    List<String> storeCategories =
        List.of("한식", "중식", "일식", "양식", "분식",
            "패스트푸드", "치킨", "피자", "베이커리", "카페", "바");

    storeCategories.forEach(storeCategory ->
        storeCategoryRepository.findByCategoryName(storeCategory)
            .orElseGet(() -> storeCategoryRepository.save(
                StoreCategory.builder()
                    .categoryName(storeCategory)
                    .build()
            )));
  }
}
