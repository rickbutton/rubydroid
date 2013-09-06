package com.rickbutton.rubydroid;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import dalvik.system.PathClassLoader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class RubySystem {

  private Context appContext;
  private ScriptingContainer container;
  private Object rubyContext;

  private static RubySystem INSTANCE;

  private RubySystem(Context appContext, Activity startActivity) {
    this.appContext = appContext;
    setupJRuby(startActivity);
  }

  public static RubySystem getInstance() {
    if (INSTANCE == null) {
      throw new RuntimeException("RubySystem not started yet");
    }
    return INSTANCE;
  }

  public static RubySystem start(Context appContext, Activity startActivity) {
    if (INSTANCE != null) {
      throw new RuntimeException("RubySystem already started");
    }
    INSTANCE = new RubySystem(appContext, startActivity);
    return INSTANCE;
  }

  public ScriptingContainer getScriptingContainer() {
    return container;
  }

  public Object getRubyContext() {
    return rubyContext;
  }

  public void requireFolder() {
    PackageManager pm = appContext.getPackageManager();
    String name = appContext.getPackageName();
    try {
      PackageInfo i = pm.getPackageInfo(name, 0);
      String dataDir = i.applicationInfo.dataDir;
      Log.e("TEST" + dataDir);
    } catch (NameNotFoundException e) {
      throw new RuntimeException("package name not found");
    }

  }

  private void setupJRuby(Activity startActivity) {
    System.setProperty("jruby.bytecode.version", "1.5");

    //enable proxy classes
    System.setProperty("jruby.ji.proxyClassFactory", "com.rickbutton.rubydroid.DalvikProxyClassFactory");
    System.setProperty("jruby.ji.upper.case.package.name.allowed", "true");
    System.setProperty("jruby.class.cache.path", appContext.getDir("dex", 0).getAbsolutePath());

    //setup jruby home
    String apkName = getApkName();
    String jrubyHome = "file:" + apkName  +"!/jruby.home";
    System.setProperty("jruby.home", jrubyHome);

    //configure jruby
    System.setProperty("jruby.compile.mode", "OFF"); // OFF OFFIR JITIR? FORCE FORCEIR
    // System.setProperty("jruby.compile.backend", "DALVIK");
    System.setProperty("jruby.bytecode.version", "1.6");
    System.setProperty("jruby.interfaces.useProxy", "true");
    System.setProperty("jruby.management.enabled", "false");
    System.setProperty("jruby.objectspace.enabled", "false");
    System.setProperty("jruby.thread.pooling", "true");
    System.setProperty("jruby.native.enabled", "false");
    System.setProperty("jruby.ir.passes", "LocalOptimizationPass,DeadCodeElimination");
    System.setProperty("jruby.backtrace.style", "normal"); // normal raw full mri

    ClassLoader loader = new PathClassLoader(apkName, RubySystem.class.getClassLoader());

    // disable gems
    RubyInstanceConfig config = new RubyInstanceConfig();
    config.setDisableGems(true);
    config.setLoader(loader);
    Ruby.newInstance(config);

    container = new ScriptingContainer(LocalContextScope.SINGLETON, 
                                            LocalVariableBehavior.PERSISTENT);
    container.setClassLoader(loader);

    Thread.currentThread().setContextClassLoader(loader);

    if (appContext.getFilesDir() != null) {
      container.setCurrentDirectory(
        appContext.getFilesDir().getPath());
    }

    container.put("$package_name", appContext.getPackageName());

    container.put("app_context", appContext);
    container.put("system", this);
    rubyContext = container.runScriptlet(PathType.CLASSPATH, "ruby/droid/init.rb");


    Object mainActivity = container.get("MainActivity");
    container.callMethod(rubyContext, "start_ruby_activity", mainActivity, startActivity);

  }

  private String getApkName() {
    //get apk name
    String packageName = "com.rickbutton.rubydroid";
    String apkName = null;
    try {
      apkName = this.appContext.getPackageManager()
        .getPackageInfo(packageName, 0).applicationInfo.sourceDir;
    } catch (PackageManager.NameNotFoundException e) {
      Log.e("Could not find package with name " + packageName);
      throw new RuntimeException(e);
    }
    return apkName;
  }

}
