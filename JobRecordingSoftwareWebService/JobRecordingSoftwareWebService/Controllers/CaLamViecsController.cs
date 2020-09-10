using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Routing;
using OneDuyKhanhDataAccess;

namespace JobRecordingSoftwareWebService.Controllers
{
    /*
    The WebApiConfig class may require additional changes to add a route for this controller. Merge these statements into the Register method of the WebApiConfig class as applicable. Note that OData URLs are case sensitive.

    using System.Web.Http.OData.Builder;
    using System.Web.Http.OData.Extensions;
    using OneDuyKhanhDataAccess;
    ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
    builder.EntitySet<CaLamViec>("CaLamViecs");
    builder.EntitySet<ThongKeCongViecTrongNgay>("ThongKeCongViecTrongNgays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class CaLamViecsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/CaLamViecs
        [EnableQuery]
        public IQueryable<CaLamViec> GetCaLamViecs()
        {
            return db.CaLamViecs;
        }

        // GET: odata/CaLamViecs(5)
        [EnableQuery]
        public SingleResult<CaLamViec> GetCaLamViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.CaLamViecs.Where(caLamViec => caLamViec.Id == key));
        }

        // PUT: odata/CaLamViecs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<CaLamViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CaLamViec caLamViec = db.CaLamViecs.Find(key);
            if (caLamViec == null)
            {
                return NotFound();
            }

            patch.Put(caLamViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CaLamViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(caLamViec);
        }

        // POST: odata/CaLamViecs
        public IHttpActionResult Post(CaLamViec caLamViec)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.CaLamViecs.Add(caLamViec);
            db.SaveChanges();

            return Created(caLamViec);
        }

        // PATCH: odata/CaLamViecs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<CaLamViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CaLamViec caLamViec = db.CaLamViecs.Find(key);
            if (caLamViec == null)
            {
                return NotFound();
            }

            patch.Patch(caLamViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CaLamViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(caLamViec);
        }

        // DELETE: odata/CaLamViecs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            CaLamViec caLamViec = db.CaLamViecs.Find(key);
            if (caLamViec == null)
            {
                return NotFound();
            }

            db.CaLamViecs.Remove(caLamViec);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/CaLamViecs(5)/ThongKeCongViecTrongNgays
        [EnableQuery]
        public IQueryable<ThongKeCongViecTrongNgay> GetThongKeCongViecTrongNgays([FromODataUri] int key)
        {
            return db.CaLamViecs.Where(m => m.Id == key).SelectMany(m => m.ThongKeCongViecTrongNgays);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool CaLamViecExists(int key)
        {
            return db.CaLamViecs.Count(e => e.Id == key) > 0;
        }
    }
}
