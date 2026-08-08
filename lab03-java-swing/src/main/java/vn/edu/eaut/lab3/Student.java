// Chạy chương trình trực tiếp qua Maven
// mvn clean compile
// mvn exec:java "-Dexec.mainClass=vn.edu.eaut.lab3.Bai08QuanLySinhVien"
// Đóng gói và chạy file JAR trên JVM
// mvn clean package
// java -jar target/lab03-java-swing-1.0-SNAPSHOT.jar
package vn.edu.eaut.lab3;

public class Student {
    private String id;
    private String name;
    private double avgScore;

    public Student(String id, String name, double avgScore) {
        this.id = id;
        this.name = name;
        this.avgScore = avgScore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getAvgScore() { return avgScore; }

    public String getRank() {
        if (avgScore >= 8.5) return "Gioi";
        if (avgScore >= 7.0) return "Kha";
        if (avgScore >= 5.0) return "Trung binh";
        return "Yeu";
    }
}