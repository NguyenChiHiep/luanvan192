//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace OneDuyKhanhDataAccess
{
    using System;
    using System.Collections.Generic;
    
    public partial class ThongKeCongViecTrongNgay
    {
        public int Id { get; set; }
        public Nullable<int> NhanVien { get; set; }
        public Nullable<System.DateTime> Ngay { get; set; }
        public Nullable<int> Ca { get; set; }
        public Nullable<int> TongGioKhongChamCong { get; set; }
        public Nullable<int> GioTinhLuong { get; set; }
        public Nullable<int> GioTinhLuongDC { get; set; }
        public string LyDoChinhGio { get; set; }
        public Nullable<int> TongThoiGianMay { get; set; }
        public Nullable<int> TongThoiGianVHMay { get; set; }
        public Nullable<int> TongGioTrongNgay { get; set; }
    
        public virtual CaLamViec CaLamViec { get; set; }
        public virtual NhanVien NhanVien1 { get; set; }
    }
}
