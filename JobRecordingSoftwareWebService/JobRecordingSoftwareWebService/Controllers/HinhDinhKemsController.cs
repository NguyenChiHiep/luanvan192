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
    builder.EntitySet<HinhDinhKem>("HinhDinhKems");
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class HinhDinhKemsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/HinhDinhKems
        [EnableQuery]
        public IQueryable<HinhDinhKem> GetHinhDinhKems()
        {
            return db.HinhDinhKems;
        }

        // GET: odata/HinhDinhKems(5)
        [EnableQuery]
        public SingleResult<HinhDinhKem> GetHinhDinhKem([FromODataUri] int key)
        {
            return SingleResult.Create(db.HinhDinhKems.Where(hinhDinhKem => hinhDinhKem.Id == key));
        }

        // PUT: odata/HinhDinhKems(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<HinhDinhKem> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            HinhDinhKem hinhDinhKem = db.HinhDinhKems.Find(key);
            if (hinhDinhKem == null)
            {
                return NotFound();
            }

            patch.Put(hinhDinhKem);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!HinhDinhKemExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(hinhDinhKem);
        }

        // POST: odata/HinhDinhKems
        public IHttpActionResult Post(HinhDinhKem hinhDinhKem)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.HinhDinhKems.Add(hinhDinhKem);
            db.SaveChanges();

            return Created(hinhDinhKem);
        }

        // PATCH: odata/HinhDinhKems(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<HinhDinhKem> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            HinhDinhKem hinhDinhKem = db.HinhDinhKems.Find(key);
            if (hinhDinhKem == null)
            {
                return NotFound();
            }

            patch.Patch(hinhDinhKem);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!HinhDinhKemExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(hinhDinhKem);
        }

        // DELETE: odata/HinhDinhKems(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            HinhDinhKem hinhDinhKem = db.HinhDinhKems.Find(key);
            if (hinhDinhKem == null)
            {
                return NotFound();
            }

            db.HinhDinhKems.Remove(hinhDinhKem);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/HinhDinhKems(5)/CongViec
        [EnableQuery]
        public SingleResult<CongViec> GetCongViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.HinhDinhKems.Where(m => m.Id == key).Select(m => m.CongViec));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool HinhDinhKemExists(int key)
        {
            return db.HinhDinhKems.Count(e => e.Id == key) > 0;
        }
    }
}
