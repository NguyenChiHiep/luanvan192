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
    
    public partial class CongViec2Set
    {
        public int Id { get; set; }
        public int SoLuongNG { get; set; }
        public int SoLuongG { get; set; }
        public System.DateTime Gio { get; set; }
        public byte[] RowVersion { get; set; }
        public int CongViec2_CongViec { get; set; }
    
        public virtual CongViec CongViec { get; set; }
    }
}
