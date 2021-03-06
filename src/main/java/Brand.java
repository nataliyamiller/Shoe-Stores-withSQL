import java.util.List;
import org.sql2o.*;
import java.util.ArrayList;


public class Brand {
  private String brand_name;
  private String style;
  private String type;
  private String color;
  private int id;


  public String getBrandName() {
    return brand_name;
  }

  public String getStyle() {
    return style;
  }

  public String getType() {
    return type;
  }

  public String getColor() {
    return color;
  }

  public int getId() {
    return id;
  }

  public Brand (String brandName, String style, String type, String color) {
    this.brand_name = brandName;
    this.style = style;
    this.type = type;
    this.color = color;
  }

  @Override
  public boolean equals(Object otherBrand) {
    if(!(otherBrand instanceof Brand)) {
      return false;
    } else {
      Brand newBrand = (Brand) otherBrand;
      return this.getBrandName().equals(newBrand.getBrandName()) &&
             this.getStyle().equals(newBrand.getStyle()) &&
             this.getType().equals(newBrand.getType()) &&
             this.getColor().equals(newBrand.getColor()) &&
             this.getId() == newBrand.getId();
    }
  }

  public static List<Brand> all() {
    String sql = "SELECT * FROM brands";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Brand.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO brands (brand_name, style, type, color) VALUES (:brand_name, :style, :type, :color)";
      this.id = (int) con.createQuery(sql, true)
      .addParameter("brand_name", this.brand_name)
      .addParameter("style", this.style)
      .addParameter("type", this.type)
      .addParameter("color", this.color)
      .executeUpdate()
      .getKey();
    }
  }

  public static Brand find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM brands WHERE id=:id";
      Brand brand = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Brand.class);
      return brand;
    }
  }

  public void updateBrandName(String brand_name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE brands SET brand_name = :brand_name WHERE id = :id";
      con.createQuery(sql)
        .addParameter("brand_name", brand_name)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void updateStyle(String style) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE brands SET style = :style WHERE id = :id";
      con.createQuery(sql)
        .addParameter("style", style)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void updateType(String type) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE brands SET type = :type WHERE id = :id";
      con.createQuery(sql)
        .addParameter("type", type)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void updateColor(String color) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE brands SET color = :color WHERE id = :id";
      con.createQuery(sql)
        .addParameter("color", color)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void addStore(Store store) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO stores_brands (store_id, brand_id) VALUES (:store_id, :brand_id)";
      con.createQuery(sql)
        .addParameter("store_id", store.getId())
        .addParameter("brand_id", this.getId())
        .executeUpdate();
    }
  }

  public List<Store> getStores() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT stores.* FROM brands JOIN stores_brands ON (brands.id = stores_brands.brand_id)" +
      " JOIN stores ON (stores_brands.store_id = stores.id) WHERE brands.id = :id";
      List<Store> brands = con.createQuery(sql)
        .addParameter("id", this.getId())
        .executeAndFetch(Store.class);
        return brands;
      }
    }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM brands WHERE id = :id;";
      con.createQuery(deleteQuery)
        .addParameter("id", id)
        .executeUpdate();

      String joinDeleteQuery = "DELETE FROM stores_brands WHERE brand_id = :brand_id";
        con.createQuery(joinDeleteQuery)
          .addParameter("brand_id", this.getId())
          .executeUpdate();
    }
  }

}
