class MainActivity < RubyActivity

  def on_create
    self.content_view = R::layout::test

    b = find_view_by_id(R::id::button)
    b.on_click_listener = proc { |view|
      Log.e("BUTTON #{view.id}")
    }
  end
end


