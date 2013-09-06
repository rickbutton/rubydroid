require 'java'

java_import 'com.rickbutton.rubydroid.Log'
java_import 'com.rickbutton.rubydroid.ProxyRubyActivity'
java_import 'com.rickbutton.rubydroid.R'
java_import 'com.rickbutton.rubydroid.RubySystem'

java_import 'android.content.Intent'
java_import 'android.app.Activity'

class RubyContext < Struct.new(:app_context, :system)

  def start_ruby_activity(activity_class, context)
    i = Intent.new
    i.set_class context, ProxyRubyActivity
    i.put_extra ProxyRubyActivity::ACTIVITY, activity_class.name
    context.start_activity i
  end

  def require_folder(folder)
    system.require_folder
  end

end

class RubyActivity
  attr_reader :java_context, :ruby_context, :a

  def start_activity(activity_class)
    ruby_context.start_ruby_activity(activity_class, java_context) 
  end

  def method_missing(meth, *args, &block)
    if java_context.respond_to? meth
      java_context.send(meth, *args)
    else
      super
    end
  end

end

context = RubyContext.new(app_context, system)

require 'ruby/activities/main_activity'

context.require_folder("activities")

return context
