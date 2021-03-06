import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.junit.rules.ExternalResource;
import org.sql2o.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends FluentTest{
  public WebDriver webDriver = new HtmlUnitDriver();
  public WebDriver getDefaultDriver(){
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Shoe Stores and Brands");
  }

  @Test
  public void formForAddingStoresIsDisplayed() {
    goTo("http://localhost:4567/stores");
    assertThat(pageSource()).contains("Add a new store");
  }

  @Test
  public void formForAddingBrandsIsDisplayed() {
    goTo("http://localhost:4567/brands");
    assertThat(pageSource()).contains("Add a new brand");
  }

  @Test
  public void storeIsSavedToDatabaseAndDisplayedOnTheStoresPage() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    String storePath = ("http://localhost:4567/stores");
    goTo(storePath);
    assertThat(pageSource()).contains("Target", "Portland, OR", "503-555-5555");
  }

  @Test
  public void brandIsSavedToDatabaseAndDisplayedOnTheBrandsPage() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    String brandPath = ("http://localhost:4567/brands");
    goTo(brandPath);
    assertThat(pageSource()).contains("Nike", "Sport", "Women's", "Yellow");
  }

  @Test
  public void storeIsDisplayedOnItsPage() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");;
    myStore.save();
    String storePath = String.format("http://localhost:4567/stores/%d", myStore.getId());
    goTo(storePath);
    assertThat(pageSource()).contains("Welcome to Target page");
  }

  @Test
  public void brandIsAddedAndDisplayedOnStoresPage() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    myStore.addBrand(myBrand);
    String storePath = String.format("http://localhost:4567/stores/%d", myStore.getId());
    goTo(storePath);
    assertThat(pageSource()).contains("Nike", "Sport", "Women's", "Yellow");
  }

  @Test
  public void updateName_UpdatesOnlyStoreNameAndLeavesAddressAndPhoneNumberUntouched() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Store savedStore = Store.find(myStore.getId());
    savedStore.updateName("New Store");
    String updatedStorePath = String.format("http://localhost:4567/stores/%d", myStore.getId());
    goTo(updatedStorePath);
    assertThat(pageSource()).contains("Welcome to New Store page");
    assertThat(pageSource()).contains("Store address: Portland, OR");
    assertThat(pageSource()).contains("Store phone number: 503-555-5555");
  }

  @Test
  public void updateAddress_UpdatesOnlyStoreAddressAndLeavesNameAndPhoneNumberUntouched() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Store savedStore = Store.find(myStore.getId());
    savedStore.updateAddress("Medford, OR");
    String updatedStorePath = String.format("http://localhost:4567/stores/%d", myStore.getId());
    goTo(updatedStorePath);
    assertThat(pageSource()).contains("Welcome to Target page");
    assertThat(pageSource()).contains("Store address: Medford, OR");
    assertThat(pageSource()).contains("Store phone number: 503-555-5555");
  }

  @Test
  public void updatePhoneNumber_UpdatesOnlyStorePhoneNumberAndLeavesNameAndAddressUntouched() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Store savedStore = Store.find(myStore.getId());
    savedStore.updatePhoneNumber("971-222-3355");
    String updatedStorePath = String.format("http://localhost:4567/stores/%d", myStore.getId());
    goTo(updatedStorePath);
    assertThat(pageSource()).contains("Welcome to Target page");
    assertThat(pageSource()).contains("Store address: Portland, OR");
    assertThat(pageSource()).contains("Store phone number: 971-222-3355");
  }

  @Test
  public void delete_DeletesStoreFromDatabaseStoreNoLongerDisplayed() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Store savedStore = Store.find(myStore.getId());
    savedStore.delete();
    String deletedStorePath = String.format("http://localhost:4567/stores");
    goTo(deletedStorePath);
    assertThat(!(pageSource()).contains("Target"));
  }

  @Test
  public void brandIsDisplayedOnItsPage() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");;
    myBrand.save();
    String brandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(brandPath);
    assertThat(pageSource()).contains("Welcome to Nike brand page");
  }

  @Test
  public void storeIsAddedAndDisplayedOnBrandsPage() {
    Store myStore = new Store("Target", "Portland, OR", "503-555-5555");
    myStore.save();
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    myBrand.addStore(myStore);
    String brandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(brandPath);
    assertThat(pageSource()).contains("Target", "Portland, OR", "503-555-5555");
  }

  @Test
  public void updateBrandName_UpdatesOnlyBrandNameAndLeavesOtherBrandDataUntouched() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    Brand savedBrand = Brand.find(myBrand.getId());
    savedBrand.updateBrandName("New Brand");
    String updatedBrandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(updatedBrandPath);
    assertThat(pageSource()).contains("Welcome to New Brand brand page");
    assertThat(pageSource()).contains("Style: Sport");
    assertThat(pageSource()).contains("Type: Women's");
    assertThat(pageSource()).contains("Color: Yellow");
  }

  @Test
  public void updateStyle_UpdatesOnlyStyleAndLeavesOtherBrandDataUntouched() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    Brand savedBrand = Brand.find(myBrand.getId());
    savedBrand.updateStyle("Wedding");
    String updatedBrandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(updatedBrandPath);
    assertThat(pageSource()).contains("Welcome to Nike brand page");
    assertThat(pageSource()).contains("Style: Wedding");
    assertThat(pageSource()).contains("Type: Women's");
    assertThat(pageSource()).contains("Color: Yellow");
  }

  @Test
  public void updateType_UpdatesOnlyTypeAndLeavesOtherBrandDataUntouched() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    Brand savedBrand = Brand.find(myBrand.getId());
    savedBrand.updateType("Men's");
    String updatedBrandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(updatedBrandPath);
    assertThat(pageSource()).contains("Welcome to Nike brand page");
    assertThat(pageSource()).contains("Style: Sport");
    assertThat(pageSource()).contains("Type: Men's");
    assertThat(pageSource()).contains("Color: Yellow");
  }

  @Test
  public void updateColor_UpdatesOnlyColorAndLeavesOtherBrandDataUntouched() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    Brand savedBrand = Brand.find(myBrand.getId());
    savedBrand.updateColor("White");
    String updatedBrandPath = String.format("http://localhost:4567/brands/%d", myBrand.getId());
    goTo(updatedBrandPath);
    assertThat(pageSource()).contains("Welcome to Nike brand page");
    assertThat(pageSource()).contains("Style: Sport");
    assertThat(pageSource()).contains("Type: Women's");
    assertThat(pageSource()).contains("Color: White");
  }

  @Test
  public void delete_DeletesBrandFromDatabaseBrandNoLongerDisplayed() {
    Brand myBrand = new Brand("Nike", "Sport", "Women's", "Yellow");
    myBrand.save();
    Brand savedBrand = Brand.find(myBrand.getId());
    savedBrand.delete();
    String deletedBrandPath = String.format("http://localhost:4567/brands");
    goTo(deletedBrandPath);
    assertThat(!(pageSource()).contains("Target"));
  }

}
