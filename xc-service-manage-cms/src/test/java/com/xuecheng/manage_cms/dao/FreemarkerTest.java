package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;//用来下载对应文件流

    @Autowired
    RestTemplate restTemplate;



    /**
     * 页面发布
     * 1.首先将前端传过来的cmsPage进行添加更新，存在就更新，不存在就添加
     * 2.保存成功后，获取保存成功的页面id
     * 3.根据页面id执行静态化
     * 通过页面id获取cmsPage对象
     * a.获取数据模型，从cmsPage对象中获得dataUrl(数据模型路径)。通过该restTemplate.getForEntity获取
     * b.获取模板，从cmsPage对选哪个中获得templateId，
     * 根据templateId获取cmdTemplate，从中获取到模板文件的templateFileId
     * 根据templateFileId往fs.chunks表中获取文件数据(GridFS)对象
     * 由gridFSBucket得到一个下载对象
     * 再由GridFsResource对象获取流
     * 再由IOUtils将该流转换成字符串对象
     * c.执行静态化
     * 创建Configuration配置类
     * 创建StringTemplateLoader模板加载器，将模板信息添加到该对象中
     * 往配置类中配置模板加载器setTemplateLoader()
     * 再获取模板
     * 执行静态化FreeMarkerTemplateUtils.processTemplateIntoString(模板，数据模型)
     * <p>
     * 4.根据静态化过的html和页面id，存储到GridFS
     * 先判断cmsPage中是否存在该对象
     * 接着将静态化过的页面字符串对象转换成流
     * 并通过gridFsTemplate.store，将静态化后的页面存储到GridFS，这时创建出一个新的htmlFileId
     * 接着更新cmsPage对象中的htmlFileId信息。保存
     * 5.向MQ发送消息
     * 先判断该cmsPage对象是否存在
     * 根据pageId创建一个map类型的消息对象
     * 转成json串
     * 获取siteId站点id
     * 往mq发送消息
     * <p>
     * 6.响应
     * 根据保存成功的cmsPage对象从中获取到站点id，再根据站点id，往cms_site站点集合中获取对象
     * 拼接
     * 站点对象中的：siteDomain+siteWebPath
     * 保存成功的cmsPage中的：pageWebPath+pageName
     */
    public void testAll(String pageId) {
        //根据传入的pageId进行判断在cmsPage中是否存在page
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (optionalCmsPage.isPresent()) {
            CmsPage cmsPage = optionalCmsPage.get();
            //获取模型数据地址
            String dataUrl = cmsPage.getDataUrl();
            //远程获取模型数据
            ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
            //获取模板信息id
            String templateId = cmsPage.getTemplateId();

            //获取templateFileId
            Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
            String templateFileId = "";
            if (optional.isPresent()){
                CmsTemplate cmsTemplate = optional.get();
                 templateFileId = cmsTemplate.getTemplateFileId();
            }
            //查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //创建下载对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getId());
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public String freemarkerTest() throws Exception {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //获取模板
        String test = findTest();
        //加载模板
        //模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", test);
        configuration.setTemplateLoader(stringTemplateLoader);
        Template template = configuration.getTemplate("template", "utf-8");
        //获取数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        ////////将组成的文件内容写到指定文件中///////
        //转化成流对象
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        //输出文件
        IOUtils.copy(inputStream, fileOutputStream);

        return content;
    }

    //数据模型
    private Map getMap() {
        Map<String, Object> map = new HashMap<>();
        //向数据模型放数据
        map.put("name", "黑马程序员");
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMondy(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMondy(200.1f);
        stu2.setAge(19);
//        stu2.setBirthday(new Date());
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放数据
        map.put("stuMap", stuMap);
        return map;
    }

    //获取模板
    public String findTest() throws IOException {
        String fileId = "620db63316dc9026247f211e";
        //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        return s;
    }
}
