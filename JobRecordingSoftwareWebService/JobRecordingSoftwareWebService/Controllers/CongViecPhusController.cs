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
    builder.EntitySet<CongViecPhu>("CongViecPhus");
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class CongViecPhusController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/CongViecPhus
        [EnableQuery]
        public IQueryable<CongViecPhu> GetCongViecPhus()
        {
            return db.CongViecPhus;
        }

        // GET: odata/CongViecPhus(5)
        [EnableQuery]
        public SingleResult<CongViecPhu> GetCongViecPhu([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecPhus.Where(congViecPhu => congViecPhu.Id == key));
        }

        // PUT: odata/CongViecPhus(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<CongViecPhu> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViecPhu congViecPhu = db.CongViecPhus.Find(key);
            if (congViecPhu == null)
            {
                return NotFound();
            }

            patch.Put(congViecPhu);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecPhuExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViecPhu);
        }

        // POST: odata/CongViecPhus
        public IHttpActionResult Post(CongViecPhu congViecPhu)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.CongViecPhus.Add(congViecPhu);
            db.SaveChanges();

            return Created(congViecPhu);
        }

        // PATCH: odata/CongViecPhus(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<CongViecPhu> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViecPhu congViecPhu = db.CongViecPhus.Find(key);
            if (congViecPhu == null)
            {
                return NotFound();
            }

            patch.Patch(congViecPhu);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecPhuExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViecPhu);
        }

        // DELETE: odata/CongViecPhus(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            CongViecPhu congViecPhu = db.CongViecPhus.Find(key);
            if (congViecPhu == null)
            {
                return NotFound();
            }

            db.CongViecPhus.Remove(congViecPhu);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/CongViecPhus(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.CongViecPhus.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool CongViecPhuExists(int key)
        {
            return db.CongViecPhus.Count(e => e.Id == key) > 0;
        }
    }
}
