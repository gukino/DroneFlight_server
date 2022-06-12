package hku.picshare.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hku.picshare.entity.Photo;
import hku.picshare.mapper.PhotoMapper;
import hku.picshare.service.PhotoService;
import org.springframework.stereotype.Service;

/**
 * @author yhp
 *
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {



}
