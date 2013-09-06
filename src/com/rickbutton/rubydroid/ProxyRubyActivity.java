package com.rickbutton.rubydroid;

import org.jruby.embed.ScriptingContainer;

import org.jruby.runtime.builtin.IRubyObject;

import android.app.Activity;
import android.os.Bundle;

public class ProxyRubyActivity extends Activity {

  public final static String ACTIVITY = "ACTIVITY";

  private Object rubyActivity;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RubySystem s = RubySystem.getInstance();
    ScriptingContainer con = s.getScriptingContainer();
    Object rubyActivityClass = con.get(getIntent().getStringExtra(ACTIVITY));

    rubyActivity = con.callMethod(rubyActivityClass, "new");

    con.put(rubyActivity, "@java_context", this);
    con.put(rubyActivity, "@a", this);
    con.put(rubyActivity, "@ruby_context", s.getRubyContext());
    con.callMethod(rubyActivity, "on_create");
  }

}
