package com.restaurant.dao;

import java.util.List;

/**
 * Interface cơ sở cho tầng Data Access Object (DAO)
 * Cung cấp các thao tác CRUD tiêu chuẩn trên các thực thể
 *
 * @param <T> Kiểu thực thể Model tương ứng
 */
public interface IBaseDAO<T> {

    /**
     * Lấy toàn bộ danh sách bản ghi
     * @return Danh sách các đối tượng thực thể
     */
    List<T> getAll();

    /**
     * Thêm mới một bản ghi vào cơ sở dữ liệu
     * @param entity Đối tượng cần lưu
     * @return true nếu thêm thành công, false nếu thất bại
     */
    boolean insert(T entity);

    /**
     * Cập nhật thông tin bản ghi đã có
     * @param entity Đối tượng chứa dữ liệu mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    boolean update(T entity);

    /**
     * Xóa một bản ghi theo khóa chính ID
     * @param id Mã định danh của bản ghi
     * @return true nếu xóa thành công, false nếu thất bại
     */
    boolean delete(int id);
}
