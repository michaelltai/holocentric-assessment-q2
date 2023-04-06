package org.holocentric.assessmentq2;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class XYZAutomation {
    private static ExtentSparkReporter sparkReporter;
    private static ExtentReports extent;
    private static ExtentTest extentTest;
    private static WebDriver driver;
    static CustomerInfo[] newCustomers = new CustomerInfo[6];

    static boolean verifyData(List<WebElement> list, CustomerInfo[] x){
        Boolean[] boolArr = new Boolean[x.length];
        Arrays.fill(boolArr,Boolean.FALSE);

        outer:
        for(int i = 0; i < list.size();i++){
            WebElement tmp = list.get(i);
            String fName = tmp.findElement(By.cssSelector("td:nth-child(1)")).getText();
            String lName = tmp.findElement(By.cssSelector("td:nth-child(2)")).getText();
            String postCode = tmp.findElement(By.cssSelector("td:nth-child(3)")).getText();
            inner:
            for(int j = 0;j<x.length;j++){
                if(fName.equals(x[j].firstName) && lName.equals(x[j].lastName) && postCode.equals(x[j].postCode)){
                    boolArr[j] = true;
                    break inner;
                }
            }
        }
        return !Arrays.asList(boolArr).contains(false);
    }

    @BeforeAll
    public static void beforeAll() throws IOException{
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver","C://Users//micha//selenium-java-4.8.3//chromedriver/chromedriver.exe");
        options.addArguments("--remote-allow-origins=*");

        extent = new ExtentReports();
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") +"\\test-output\\testReport.html");
        extent.attachReporter(sparkReporter);

        sparkReporter.config().setOfflineMode(true);
        sparkReporter.config().setDocumentTitle("XYZ Automation Report");
        sparkReporter.config().setReportName("Test Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
        sparkReporter.config().setEncoding("UTF-8");



        //1. DECLARE CUSTOMERS
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        newCustomers[0] = new CustomerInfo("Kyo","Kusanagi", "L789C349");
        newCustomers[1] = new CustomerInfo("Kyo","Mina","M098Q585");
        newCustomers[2] = new CustomerInfo("Lisa","Marnie","L789C349");
        newCustomers[3] = new CustomerInfo("Lola","Rose","A897N450");
        newCustomers[4] = new CustomerInfo("Jackson","Connely","L789C349");
        newCustomers[5] = new CustomerInfo("Mariotte","Tova","L789C349");
    }


    @Test
    public void runTest(){
        extentTest = extent.createTest("XYZ Bank Test Run");

        try{
            //2. GO TO RESPECTIVE SITE
            driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login.");
            WebElement bankManagerBtn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@class='btn btn-primary btn-lg'])[2]")));
            bankManagerBtn.click();
            WebElement addCustBtn =  new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" (//button[@class='btn btn-lg tab'])[1]")));
            addCustBtn.click();
            WebElement firstNameInput =  new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='First Name']")));
            WebElement lastNameInput =  new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Last Name']")));
            WebElement postcodeInput =  new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Post Code']")));
            WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));

            // 3. ADD NEW CUSTOMERS
            for(CustomerInfo e : newCustomers){
                firstNameInput.sendKeys(e.firstName);
                lastNameInput.sendKeys(e.lastName);
                postcodeInput.sendKeys(e.postCode);
                submitBtn.click();
                driver.switchTo().alert().accept();
            }

            // 4. VIEW CUSTOMERS
            WebElement viewCusBtn =  new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" (//button[@class='btn btn-lg tab'])[2]")));
            viewCusBtn.click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            WebElement customerList = driver.findElement(By.tagName("tbody"));
            List<WebElement> customerDetails = customerList.findElements(By.tagName("tr"));

            // 5. VERIFY ALL NEW CUSTOMERS ARE ADDED
            if(verifyData(customerDetails,newCustomers)){
                System.out.println("All new customers are inserted to the table");
            }else{
                System.out.println("All new customers are not inserted to the table");
            }

            // 6. REMOVE SPECIFIC CUSTOMERS
            for(WebElement e : customerDetails){
                String fName = e.findElement(By.cssSelector("td:nth-child(1)")).getText();
                String lName = e.findElement(By.cssSelector("td:nth-child(2)")).getText();
                String postCode = e.findElement(By.cssSelector("td:nth-child(3)")).getText();

                if((fName.equals("Jackson") && lName.equals("Connely") && postCode.equals("L789C349")) || (fName.equals("Mariotte") && lName.equals("Tova") && postCode.equals("L789C349"))){
                    WebElement deleteBtn = e.findElement(By.cssSelector("button[ng-click=\"deleteCust(cust)\"]"));
                    deleteBtn.click();
                    System.out.println(fName + " " + lName + " has been removed");
                }
            }

            extentTest.log(Status.PASS, "Test Run Pass");
        } catch (Exception ex){
            extentTest.log(Status.FAIL,ex);
            throw ex;
        }
    }

    @AfterAll
    public static void afterAll(){
        if(driver !=null){
            driver.quit();
        }

        extent.flush();
    }
}


class CustomerInfo {

    public String firstName;
    public String lastName;
    public String postCode;

    CustomerInfo(String firstName, String lastName, String postCode){
        this.firstName = firstName;
        this.lastName = lastName;
        this.postCode = postCode;
    }
}