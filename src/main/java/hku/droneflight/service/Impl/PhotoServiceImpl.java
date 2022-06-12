package hku.droneflight.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hku.droneflight.entity.Photo;
import hku.droneflight.mapper.PhotoMapper;
import hku.droneflight.service.PhotoService;
import org.springframework.stereotype.Service;

/**
 * @author yhp
 *
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {



}
