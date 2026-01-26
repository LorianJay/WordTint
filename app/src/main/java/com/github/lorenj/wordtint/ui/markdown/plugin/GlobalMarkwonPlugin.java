package com.github.lorenj.wordtint.ui.markdown.plugin;

import android.content.Context;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.ui.markdown.handler.APPLinkResolver;
import com.github.lorenj.wordtint.ui.markdown.handler.SpanHandler;
import com.github.lorenj.wordtint.ui.markdown.movementmethod.ClickableSpanMovementMethod;
import com.github.lorenj.wordtint.ui.markdown.spanfactory.AppHeadingSpanFactory;
import com.github.lorenj.wordtint.ui.markdown.spanfactory.AppLinkSpanFactory;
import com.github.lorenj.wordtint.R;

import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.destination.ImageDestinationProcessor;

/**
 * 调用链路:<br>
 * 注意Node实际上类似于链表<br>
 * 1、configure(做插件配置的)->2、configureParser->3、configureTheme(配置主题信息)<br>
 * 4、configureConfiguration(配置解析器功能的,几乎不需要我们做什么,唯一的build方法实际上就是如果你有自已MarkwonSpansFactory可以调用该方法)<br>
 * 5、configureVisitor(配置Visitor)->6、configureSpansFactory(配置Spans工厂的,通过工厂的getSpans方法来渲染节点,因为Spans无法复用)<br>
 * 7、processMarkdown(预解析markdown)->8、beforeRender(在渲染某个具体的节点之前调用该方法)<br>
 * 9、afterRender(但所有节点渲染完毕后会调用该方法,这是因为这个框架会在渲染节点的时候是像类链表的形式去渲染节点,所以渲染完毕后是整个一条链上都渲染完毕了),所以在这个层面通过visitor你还是可以手动地取修改最终渲染的结果,但个人感觉意义不大
 * 但是在HTML插件中,所有的页面标签解析完毕后是统一在afterRender下进行渲染的<br>
 * 10、beforeSetText->11、afterSetText<br>
 * 解析的时候分为很多层去解析:<br>
 * 第一层是document,document的第一个子孩子是paragraph,相当于document是一个大框,但是我们不能直接通过这个大框来获取到'软件运行中所涉及到的权限:'这个Heading,我们只能通过paragraph的next方法来获取到document大框下面的所有节点(类似链表的方式).<br>
 * 接着paragraph又是一个大框,这个大框里面的第一个子孩子是Text节点的这段话'我们重视你的个人信息安全和隐私保护.我们在',然后我们又能根据这个大框下面的第一个自孩子的next方法来得到&lt;span&gt;标签对应的Node节点.<br>
 * 还要注意Node和Block的区别,Node一定是依附于Block的,Block相当一一个块,而这个块里面也可以拥有很多Node.<br>
 * 在解析的过程中会动态地设置Span,但是对于HTML标签而言只有遇到结束标签的时候才会设置span.<br>
 *
 * @author cnsukidayo
 * @date 2023/1/12 12:20
 */
public class GlobalMarkwonPlugin extends AbstractMarkwonPlugin {
    private final Context context;

    private GlobalMarkwonPlugin(Context context) {
        this.context = context;
    }

    @NonNull
    public static MarkwonPlugin create(Context context) {
        return new GlobalMarkwonPlugin(context);
    }

    @Override
    public void configure(@NonNull Registry registry) {
        /*
        一个插件当中还可以配置多个插件,相当于插件继承? 1
        第一个参数代表当前插件需要哪一个插件,第二个参数实际上是一个回调函数,它会把第一个申请到的插件传入第二个参数的apply实现类方法.
        并且如果当前的markwon没有使用第一个参数对应的插件,则这里会报错.
        并且该方法应该会在一开始就调用
        */
        registry.require(HtmlPlugin.class).addHandler(new SpanHandler(context));
        registry.require(CorePlugin.class).hasExplicitMovementMethod(true);
        //registry.require(ImagesPlugin.class, new Action<ImagesPlugin>() {
        //    @Override
        //    public void apply(@NonNull ImagesPlugin imagesPlugin) {
        //        imagesPlugin.addSchemeHandler(INetworkSchemeHandler.create());
        //    }
        //});
        super.configure(registry);
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        // 2
    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
        // 3
        builder.isLinkUnderlined(false);
        builder.headingBreakHeight(0);

        // custom 自定义的一些属性
        final float[] absoluteHeadingSize = new float[6];
        absoluteHeadingSize[0] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h1);
        absoluteHeadingSize[1] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h2);
        absoluteHeadingSize[2] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h3);
        absoluteHeadingSize[3] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h4);
        absoluteHeadingSize[4] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h5);
        absoluteHeadingSize[5] = context.getResources().getDimensionPixelSize(R.dimen.markwon_heading_h6);
        builder.headingTextSizeMultipliers(absoluteHeadingSize);
        // import!注意这里的步骤是必须的,必须在原有框架提供的Builder全部设置完毕之后,才能调用该方法配置自定义的Theme
        configureAppTheme(builder);
    }


    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        // 配置解析器功能
        builder.linkResolver(new APPLinkResolver());
        builder.imageDestinationProcessor(new ImageDestinationProcessor() {
            @NonNull
            @Override
            public String process(@NonNull String destination) {
                return AnyLanguageWordProperties.imagePrefix + destination;
            }
        });
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        /* 5
        调用完beforeRender方法后会调用Visitor来访问每个节点
        configureVisitor主要的作用在于,可以对当前节点设置其具体的Spanned对象
        注意一个节点只能配置一个NodeVisitor,我们可以通过NodeVisitor来决定当前的节点使用哪个Spanned
        所以这个方法对节点的操控性非常强.
        */
        builder.on(Heading.class, new MarkwonVisitor.NodeVisitor<Heading>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull Heading heading) {
                // 计算是否需要换行
                visitor.blockStart(heading);
                // 获取当前文本的长度,并且此时链接对应的文字还没有添加上去呢,因为保不齐当前Heading段里面还有其它的标签,这些子标签都要受到未来HeadingSpan的影响
                final int length = visitor.length();
                // 添加链接对应的文字,此时的visitor.length();已经不是刚才的长度了.
                visitor.visitChildren(heading);

                CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());
                // 很关键的一步操作,这一步就是真正调用SpanFactory然后设置Span
                visitor.setSpansForNodeOptional(heading, length);
                // 计算是否需要换行
                visitor.ensureNewLine();
            }
        });

    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        /* 6
        工厂相当于用来处理每个节点,如果有args0所代表的节点,则使用该节点解析器.注意SpanFactory是用来渲染节点的,而不是用于处理节点功能的.
        addFactory方法已被废弃,实则内部调用prependFactory方法,就是为某个节点添加多个SpanFactory.
        appendFactory和prependFactory本质上没有区别,就是当前工厂是添加到第一个使用的工厂还是最后一个使用的工厂.
        RenderProps是一个集合,存储的内容是某个Node解析完毕之后将其所有的状态存储到RenderProps中,通过RenderProps来获取存储在其中的Node的值.
        因为不同的Node有不同的状态值类型,它们是通过Prop来区分的,而RenderProps是通过Map来获取value的,Map的key就是Prop,也就是通过状态来获取状态值.
        但是真正获取的时候是通过CoreProps先区分类型,然后再将RenderProps传入获取具体的值.
        这个方法就是用来返回工厂的,针对不同的状态返回不同的渲染工厂.
        requireFactory
        getFactory 这两个方法本质上没有区别

        */
//        builder.setFactory(Heading.class, (configuration, props) -> new NoUnderLineHeadingSpan(configuration.theme(), CoreProps.HEADING_LEVEL.get(props)));

        builder.setFactory(Link.class, new AppLinkSpanFactory());
        builder.setFactory(Heading.class, new AppHeadingSpanFactory());
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        // markDown的预解析,markdown也就是当前的markdown全文
        return super.processMarkdown(markdown);
    }


    @Override
    public void beforeRender(@NonNull Node node) {
        /*
        当一个节点Node解析之前会先调用该方法,你可以在该方法内检查或修改节点
        第一步:先解析节点,节点会被解析为Node节点
        第二步:根据解析出的Node节点,将其渲染为Spanned实例.
        而Spanned实例是通过SpannableBuilder、SpanFactory共同进行构建的.
        第三部:将Spanned实例应用到TextView上,Spanned是Android原生对象,也就是说最终渲染的对象是Spanned
        Node仅仅是对原文进行解析后解析出的对象.
        */
        super.beforeRender(node);
    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
        // 当一个节点解析完成之后会调用该方法,你可以在该方法内检查节点,该方法调用在Visitor之后
        super.afterRender(node, visitor);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        // 在一个Spanned应用到TextView时会调用该方法
        super.beforeSetText(textView, markdown);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        // 在markdown应用到一个textView之后调用该方法,走到这一步基本上已经结束了
        textView.setMovementMethod(ClickableSpanMovementMethod.getInstance());
        super.afterSetText(textView);
    }

    public void configureAppTheme(MarkwonTheme.Builder builder) {
        MarkwonThemeAdapter.createAdapterInstance(builder);
        MarkwonThemeAdapter markwonThemeAdapter = MarkwonThemeAdapter.getAdapterInstance();
        markwonThemeAdapter.setBetweenHeadingHeight(context.getResources().getDimensionPixelSize(R.dimen.markwon_between_heading_height));
    }

}
