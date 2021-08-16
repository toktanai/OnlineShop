package java_spring_boot.hw7.db;

import java.sql.Date;
import java.util.ArrayList;

public class DBManager {
    private static ArrayList<ShopItem> shopItems = new ArrayList<>();

    static {
        shopItems.add(new ShopItem(1L,"IPhone 11 Pro Max","IPhone 11 Pro Max, 512GB, 5G Network, Display 6.7", 2499, 50,5,"https://tntmusic.ru/media/content/article/2020-07-27_19-05-56__330cbe86-d03c-11ea-996a-e9e448f646dc.jpg"));
        shopItems.add(new ShopItem(2L,"MEIZU 16 Pro","MEIZU 16 Pro, Screen 6.2, 2232x1080 pixels, 8 GB CPU RAM", 599, 89,3,"https://theroco.com/assets/2019/08/meizu-16s-pro-main.jpg"));
        shopItems.add(new ShopItem(3L,"Samsung Galaxy Note 20","Samsung Galaxy Note 20, Galaxy Fold 2, Z Flip 5G, FullHD, 256 GB Memory", 799, 75,4,"https://mobiltelefon.ru/photo/september19/13/raspakovka_samsung_galaxy_note_10_na_snapdragon_i_exynos_video_picture4_0.jpg"));
        shopItems.add(new ShopItem(4L,"Nokia 9 Pureview","Nokia 9 Pureview, SoC Qualcomm Snapdragon 845, GPU Adreno 630 ", 249, 47,1,"https://theroco.com/assets/2019/01/nokia-9-pureview-release-date-1.jpg"));
//        shopItems.add(new ShopItem(5L,"XIAOMI Redmi Note 9","XIAOMI Redmi Note 9, 6 GB CPU RAM, 128 GB Memory", 349, 120,4,"https://mihome.kz/images/redmi-note-9-pro-new/1.jpg"));

    }

    private static Long id = 5L;

    public static ArrayList<ShopItem>getShopItems(){
        return shopItems;
    }

    public static void addShopItems(ShopItem shopItem){
        shopItem.setId(id);
        shopItems.add(shopItem);
        id++;
    }
}
