package danbroid.busapp.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import danbroid.busapp.okhttpClient
import java.io.InputStream


@GlideModule
class GlideSupport : AppGlideModule() {
  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    log.warn("registerComponents() $glide")
    registry.replace(
      GlideUrl::class.java,
      InputStream::class.java,
      OkHttpUrlLoader.Factory(context.okhttpClient)
    )
  }

/*  override fun applyOptions(context: Context, builder: GlideBuilder) {
    log.trace("applyOptions() ${builder}")
    super.applyOptions(context, builder)
  }*/
}

private val log =
  org.slf4j.LoggerFactory.getLogger(GlideSupport::class.java)
