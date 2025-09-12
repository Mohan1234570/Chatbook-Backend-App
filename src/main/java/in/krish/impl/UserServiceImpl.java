//package in.krish.impl;
//
//public UserProfileDTO getUserProfile(Long userId) {
//    User user = userRepo.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    UserProfileDTO dto = new UserProfileDTO();
//    dto.setUsername(user.getUsername());
//    dto.setEmail(user.getEmail());
//
//    int totalLikes = 0;
//    int totalComments = 0;
//
//    List<PostDTO> postDTOs = new ArrayList<>();
//    for (Post post : user.getPosts()) {
//        PostDTO postDTO = new PostDTO();
//        postDTO.setTitle(post.getTitle());
//        postDTO.setContent(post.getContent());
//        postDTO.setCreatedDate(post.getCreatedDate());
//        postDTO.setLikeCount(post.getLikes().size());
//        postDTO.setCommentCount(post.getComments().size());
//
//        totalLikes += post.getLikes().size();
//        totalComments += post.getComments().size();
//
//        postDTOs.add(postDTO);
//    }
//
//    dto.setPosts(postDTOs);
//    dto.setTotalPosts(user.getPosts().size());
//    dto.setTotalLikes(totalLikes);
//    dto.setTotalComments(totalComments);
//
//    return dto;
//}
