package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTest {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    CmsConfigRepository cmsConfigRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Test
    public void saveTest() throws Exception {
        //要存储的文件
        File file = new File("D:\\han\\itapp\\xcEduService01\\test-freemarker\\src\\main\\resources\\templates\\test1.ftl");
        //定义输入流
        FileInputStream fi = new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(fi, "测试模板");
        System.out.println(objectId.toString());
    }

    //查询存入的文件
    @Test
    public void findTest() throws IOException {
        String fileId = "620efd4816dc9004b882bbde";
        //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        System.out.println(s);
    }


    //删除文件
    @Test
    public void testDelFile() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("620efb6216dc901cf4c1e6ed")));
    }


    //往mongodb添加模板的时候，创建与之模板名称一样的cmsPage对象，并记录创建时间和路径
    @Test
    public void addGridFSTemplate(){
        String filePath = "D:\\han\\itapp\\xcEduService01\\test-freemarker\\src\\main\\resources\\templates\\test1.ftl";
        File f = new File(filePath);
        if (!f.exists()){
            new Throwable("不存在");
        }
        //获取文件名称
        String[] strings = fileName(filePath);
        String templateId = "";
        try {
//            String name = f.getName();
//            String fileName = name.substring(0, name.indexOf("."));
            FileInputStream fileInputStream = new FileInputStream(filePath);
//            FileInputStream fileInputStream = new FileInputStream(f);
            //向GridFS存储文件
            ObjectId objectId = gridFsTemplate.store(fileInputStream, "han",null,null);
            String templateFileId = objectId.toHexString();
            //更新cmsTemplate
            Optional<CmsTemplate> byId = cmsTemplateRepository.findById(templateFileId);
            if (!byId.isPresent()){
                CmsTemplate cmsTemplate = new CmsTemplate();
                cmsTemplate.setSiteId("5a751fab6abb5044e0d19ea1");
                cmsTemplate.setTemplateFileId(templateFileId);
                cmsTemplate.setTemplateName("han");
                CmsTemplate save = cmsTemplateRepository.save(cmsTemplate);
                if (save!=null){
                     templateId = save.getTemplateId();
                }
            }
            //更新cmsPage
            CmsPage findCmsPage = cmsPageRepository.findByPageName(strings[0]+".html");
            if (findCmsPage==null){
                CmsPage cmsPage = new CmsPage();
                cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
                cmsPage.setTemplateId(templateId);
                cmsPage.setPageCreateTime(new Date());
                cmsPage.setPageName(strings[0]+".html");
                cmsPage.setPageAliase("han_test");
                cmsPage.setPagePhysicalPath(strings[1]);
                cmsPage.setPageType("0");
                cmsPage.setDataUrl("http://localhost:31001/cms/page/get/620e07a816dc90170c3a5d68");

                CmsPage save = cmsPageRepository.save(cmsPage);
                if (save!=null){
                    System.out.println("save success");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public  String[] fileName(String filePath){
        //获取文件名称
        int i = filePath.lastIndexOf("\\");
        int i1 = filePath.lastIndexOf(".");
        String fileName = filePath.substring(i+1,i1);
        String[] strings = new String[2];
        strings[0] = fileName;//存入文件名
        String path = filePath.substring(0, i);
        strings[1] = path;
        return strings;
    }
}
